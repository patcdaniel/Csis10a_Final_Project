package axonTextTool;

import java.util.ArrayList;
import java.util.List;
/**
 * Trace Objects store the data for a single stimulation event.
 * @author Patrick Daniel
 * @date May 2014
 */
public class Trace{
    private String filename;
    private double startTime;
    private int sweepNum;
    private List<Double> voltageList;
    private List<Double> timeList;
    private List<Double> currentList;

    public Trace(String filename, int sweepNum){
        this.filename = filename;
        this.sweepNum = sweepNum;
    }

    public List<Double> getVoltageList(){
        return this.voltageList;
    }

    public List<Double> getTimeList() {
        return this.timeList;
    }

    public List<Double> getCurrentList() {
        return this.currentList;
    }

    public String getFilename() {
        return this.filename;
    }

    public double getStartTime(double sTime) {
       return this.startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public void setTime(ArrayList<Double> time) {
//        this.time = convertList(time);
        this.timeList = time;
    }

    public void setVoltageData(ArrayList<Double> voltage) {
//        this.voltageData = convertList(voltage);
//        this.voltageList = voltage;
        this.voltageList = voltage;
    }

    public void setCurrentData(ArrayList<Double> current) {
        this.currentList = current;
    }

    public int getSweepNum() {
        return this.sweepNum;
    }


}
