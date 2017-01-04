package odu.lane_detection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by marab on 10/8/2015.
 */

public class LaneEvent {
    MotionEventManager manager;

    public LaneEvent(MotionEventManager manager, String filePath) {
        this.manager = manager;
        this.csvFile = filePath;
    }

    String csvFile = "";
    BufferedReader reader = null;
    String line= "";
    String cvsSplitBy = ",";
    String state = "noBump";


    int index = 0;
    double gyroY;
    double time;
    double latitude;
    double longitude;
    double gpsSpeed;
    double Ts;
    double midGyroZ;


    ArrayList<Double> gyroYDatalist = new ArrayList<Double>();
    ArrayList<Double> gyroYRawDatalist = new ArrayList<Double>();
    ArrayList<Double> gyroZDatalist = new ArrayList<Double>();
    ArrayList<Double> gpsSpeedList = new ArrayList<Double>();

    ArrayList<Double> timeDatalist = new ArrayList<Double>();
    ArrayList<Double> bumpDatalist = new ArrayList<Double>();
    ArrayList<Double> bumpDatalist2 = new ArrayList<Double>();

    ArrayList<Double> latitudelist = new ArrayList<Double>();
    ArrayList<Double> longitudelist = new ArrayList<Double>();


    int start = 0;
    int end = 0;
    int start2 = 0;
    int end2 = 0;
    double Tdwell = 0;


    public void process() throws IOException {
        reader = new BufferedReader(new FileReader(csvFile));
        reader.readLine();
        reader.readLine();
        while ((line = reader.readLine()) != null) {

            String[] dataRow = line.split(cvsSplitBy);

            time = Double.parseDouble(dataRow[0]);
            latitude = Double.parseDouble(dataRow[1]);
            gpsSpeed = Double.parseDouble(dataRow[2]);
            longitude = Double.parseDouble(dataRow[3]);
            gyroY = Double.parseDouble(dataRow[5]);
            midGyroZ = Double.parseDouble(dataRow[7]);

            timeDatalist.add(time);
            latitudelist.add(latitude);
            gpsSpeedList.add(gpsSpeed);
            longitudelist.add(longitude);
            gyroYDatalist.add(Math.abs(gyroY));
            gyroYRawDatalist.add(gyroY);
            gyroZDatalist.add(midGyroZ);


        }

        while(index < gyroYDatalist.size()-1)
        {
            if(state == "noBump" &&  gyroYDatalist.get(index) > 0.03 )
            {
                state = "oneBump";
                start = index;
                manager.OnMotionEvent(new SimpleMotionEvent(longitudelist.get(index), latitudelist.get(index)));
                index++;
            }
            else if(state == "oneBump" )
            {
                bumpDatalist.clear();
                manager.OnMotionEvent(new SimpleMotionEvent(longitudelist.get(index), latitudelist.get(index)));
                while(gyroYDatalist.get(index) >= 0.03)
                {
                    bumpDatalist.add(gyroYDatalist.get(index));
                    manager.OnMotionEvent(new SimpleMotionEvent(longitudelist.get(index), latitudelist.get(index)));
                    index++;
                    if (index >= gyroYDatalist.size())
                        break;
                }
                if((index < gyroYDatalist.size()) && (gyroYDatalist.get(index) < 0.03))
                {
                    end = index;
                    if(valid(timeDatalist,bumpDatalist,start,end))
                    {
                        state = "waiting for bump";
                    }
                    else
                    {
                        state = "noBump";
                    }
                }
                if (index < longitudelist.size())
                    manager.OnMotionEvent(new SimpleMotionEvent(longitudelist.get(index), latitudelist.get(index)));
                index++;
            }
            else if(state  == "waiting for bump" && gyroYDatalist.get(index) > 0.03)
            {
                Tdwell = timeDatalist.get(index) - timeDatalist.get(end);

                if(Tdwell < 3 )
                {
                    bumpDatalist2.clear();
                    start2 = index;
                    while(gyroYDatalist.get(index) >= 0.03)
                    {
                        bumpDatalist2.add(gyroYDatalist.get(index));
                        manager.OnMotionEvent(new SimpleMotionEvent(longitudelist.get(index), latitudelist.get(index)));
                        index++;
                        if (index >= gyroYDatalist.size())
                            break;
                    }
                    if((index < gyroYDatalist.size()) && (gyroYDatalist.get(index) < 0.03))
                    {
                        end2 = index;
                        if(valid(timeDatalist,bumpDatalist2,start,end))
                        {
                           // System.out.println("Lane Change ends at " + (index));
                           // CalculateLaneSwichDisplacements(start, end, start2, end2, ...);

                            if (gyroYRawDatalist.get(index)< 0 ) {
                                System.out.print("leftLaneswitch");
                                manager.OnMotionEvent(new LeftLaneSwitchEvent((byte) 1, longitudelist.get(index), latitudelist.get(index)));
                            }
                            else if (gyroYRawDatalist.get(index)>0)
                            {
                                System.out.print("RightLaneswitch");
                                manager.OnMotionEvent(new RightLaneSwitchEvent((byte) 1, longitudelist.get(index), latitudelist.get(index)));
                            }
                        }
                        else
                        {
                           // System.out.println("Turn finish at" + (index+1));
                           // CalculateTurnDisplacements(start, end, gyroYRawDatalist,);

                            if (gyroYRawDatalist.get(index)> 0 )
                            {
                                System.out.print("leftTurn");
                                manager.OnMotionEvent(new LeftTurnEvent(longitudelist.get(index), latitudelist.get(index)));
                            }
                            else if (gyroYRawDatalist.get(index)<0)
                            {
                                System.out.print("RightTurn");
                                manager.OnMotionEvent(new RightTurnEvent(longitudelist.get(index), latitudelist.get(index)));
                            }
                        }

                    }
                    state = "noBump";
                }
                else if(Tdwell > 3)
                {
                    if (gyroYRawDatalist.get(end)> 0 )
                    {
                        System.out.print("leftTurn from " +start+"  to   "+end+"   ." );
                        manager.OnMotionEvent(new LeftTurnEvent(longitudelist.get(start), latitudelist.get(start)));
                    }
                    else if (gyroYRawDatalist.get(end)<0)
                    {
                        System.out.print("RightTurn");
                        manager.OnMotionEvent(new RightTurnEvent(longitudelist.get(start), latitudelist.get(start)));
                    }
                    state = "noBump";
                }
                else
                {
                    state = "waiting for bump";
                }
            }
            else
            {
                manager.OnMotionEvent(new SimpleMotionEvent(longitudelist.get(index), latitudelist.get(index)));
                index++;
                continue;
            }
        }
    }

    boolean valid( ArrayList<Double> timeDatalist, ArrayList<Double> bumpDatalist, int start, int end)
    {
        if( ! bumpDatalist.isEmpty())
        {
            // System.out.println(start + " " + end );
            double Tbump = timeDatalist.get(end) - timeDatalist.get(start);

            Collections.sort(bumpDatalist);
            double a1 = bumpDatalist.get(bumpDatalist.size() - 1);
            if(Tbump>1.5 && a1>0.06)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }
}
