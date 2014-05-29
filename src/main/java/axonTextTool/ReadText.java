package axonTextTool;
/**
 * Object to import and create Trace objects from.
 * @author Patrick Daniel
 * @date May 2014
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadText {

    private File filename;
    private int colNum;
    private ArrayList<Double>[] arrayLists; //This array of Lists is needed to capture each column in whole from the text files
    private Trace[] experiment;

    public ReadText(File filename) {
        this.filename = filename;
    }

    private void setArrayLists() {
        this.arrayLists = new ArrayList[this.colNum];

        System.out.println("The length of the arrayList is: " + arrayLists.length);
    }

    private void initializeArrayList() {
        for (int i = 0; i < this.arrayLists.length; i++) {
            this.arrayLists[i] = new ArrayList<Double>();
        }
    }

    /**
     * Loads text file and creates Trace object for each event
     * @param file File object for the text file to be loaded
     * @throws IOException
     * @throws FileNotFoundException
     */

    public void readFile(File file) throws IOException, FileNotFoundException { //IOException has to been here for FileReader!!
        String nextLine = ""; //Will store each line
        int lineIndex = 0; //Store the current line number
        Trace[] experimentData = new Trace[0];
        String filename = file.getName();
        boolean firstData = false;
        Scanner s = new Scanner(new BufferedReader(new FileReader(file.getAbsoluteFile())));
        s.useDelimiter("\n"); // Treat newLine character as the delimited, returning single line tokens
        ArrayList<Double>[] arrayLists;
        while(s.hasNext()) {
            nextLine = s.nextLine();
            if(isHeader(nextLine,lineIndex)) {
                if(lineIndex == 1) { //The second line of the header has the number of columns
                    String[] splitLine = nextLine.split("\t"); //File is tab delimited
                    this.colNum = Integer.parseInt(splitLine[1].trim());
                    setArrayLists();
                    initializeArrayList();
                    this.colNum = (colNum - 1)/2; //want a Trace object for each stimulation event, this includes an current and a voltage Trace
                    experimentData = makeTraces(colNum,filename); //now have a list
                    System.out.println("There are " + experimentData.length + " different traces for this experiment.");
                }
                else if (nextLine.startsWith("\"SweepStartTimesMS")){
                    parseStartTimes(nextLine, experimentData);
                }
            }
            else{
                parseData(nextLine);
            }
            lineIndex++;
        }
        setTraceData(experimentData);
        this.experiment =experimentData;
    }

    /**
     * Check if the buffered stream in still in the header section of the textfile by looking for a single quotation at the start of the line
     * @param inLine String containing an entire line from an experiment
     * @return Boolean
     */
    private static Boolean isHeader(String inLine, int lineIndex) {
        return (inLine.startsWith("\"") || lineIndex < 2 ); //Check to
    }

    /**
     * Initialize an array of Traces and assign the sweep number and filename
     * @param sweepNum Int storing the number of sweep from a stimulation
     * @param filename Filename of the text file being processed
     * @return Trace[]
     */
    private static Trace[] makeTraces(int sweepNum, String filename) {
        Trace[] experData = new Trace[sweepNum];
        for (int i = 0; i < sweepNum; i++) {
            experData[i] = new Trace(filename,i+1);
        }
        return experData;
    }

    /**
     * Parse up a string from the header containing the Start times for each sweep and use the Trace.setStartTime modifier
     * @param startTimes String type
     * @param experimentData Trace array
     */
    private void parseStartTimes(String startTimes, Trace[] experimentData) {
        String subTimes = "";
        for (int i = 0; i < startTimes.length(); i++) { //Maybe replace with RegEx if time allows
            if(startTimes.charAt(i) == '=') {
                subTimes = startTimes.substring(i+1,startTimes.length()-1); //remove the lead of the string return
                break;
            }
        }
        String[] strTimes = subTimes.split(",");
        if(experimentData.length == strTimes.length) {
            for (int i = 0; i < strTimes.length; i++) {
                double strTime = Double.parseDouble(strTimes[i]);
                experimentData[i].setStartTime(strTime);
            }
        }
    }

    /**
     * parseData breaks up a string and appends to arrayLists.
     * @param dataLine Single buffered line from text file
     */
    private void parseData(String dataLine) {
        String[] dataArray =  dataLine.split("\t"); //Split up the buffered line, to a String array
        for (int i = 0; i < this.arrayLists.length; i++) {
            this.arrayLists[i].add(Double.parseDouble(dataArray[i]));
        }
    }

    /**
     * setTraceData uses Trace set methods to set teh Data
     * @param experimentData Array of Trace objects to be populated with data
     */
    public void setTraceData(Trace[] experimentData) {
        int j = 1;
        for (int i = 0; i < experimentData.length; i++) {
            experimentData[i].setTime(this.arrayLists[0]);
            experimentData[i].setVoltageData(this.arrayLists[j]); //First Voltage Data starts at index 1
            experimentData[i].setCurrentData(this.arrayLists[j+1]);
            j=j+2;
        }
    }

    public Trace[] getExperiment() {
        return this.experiment;
    }
}
