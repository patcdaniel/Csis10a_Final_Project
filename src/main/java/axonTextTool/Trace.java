package axonTextTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 5/19/14.
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
//        this.currentData = convertList(current);
        this.currentList = current;
    }

    public int getSweepNum() {
        return this.sweepNum;
    }


}
