package com.example.yhourstaffproject.object;

public class Week {
    String id;
    String mon1;
    String mon2;
    String mon3;
    String tue1;
    String tue2;
    String tue3;
    String wed1;
    String wed2;
    String wed3;
    String thu1;
    String thu2;
    String thu3;
    String fri1;
    String fri2;
    String fri3;
    String sat1;
    String sat2;
    String sat3;
    String sun1;
    String sun2;
    String sun3;
    String morningSStart;
    String morningSend;
    String afternoonSStart;
    String afternoonSend;
    String eveningSStart;
    String eveningSend;
    String startDay;
    String endDay;


    public Week(String id, String startDay, String endDay){
        this.id = id;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public Week(String id, String mon1, String mon2, String mon3, String tue1, String tue2, String tue3, String wed1, String wed2, String wed3, String thu1, String thu2, String thu3, String fri1, String fri2, String fri3, String sat1, String sat2, String sat3, String sun1, String sun2, String sun3, String morningSStart, String morningSend, String afternoonSStart, String afternoonSend, String eveningSStart, String eveningSend, String startDay, String endDay) {
        this.id = id;
        this.mon1 = mon1;
        this.mon2 = mon2;
        this.mon3 = mon3;
        this.tue1 = tue1;
        this.tue2 = tue2;
        this.tue3 = tue3;
        this.wed1 = wed1;
        this.wed2 = wed2;
        this.wed3 = wed3;
        this.thu1 = thu1;
        this.thu2 = thu2;
        this.thu3 = thu3;
        this.fri1 = fri1;
        this.fri2 = fri2;
        this.fri3 = fri3;
        this.sat1 = sat1;
        this.sat2 = sat2;
        this.sat3 = sat3;
        this.sun1 = sun1;
        this.sun2 = sun2;
        this.sun3 = sun3;
        this.morningSStart = morningSStart;
        this.morningSend = morningSend;
        this.afternoonSStart = afternoonSStart;
        this.afternoonSend = afternoonSend;
        this.eveningSStart = eveningSStart;
        this.eveningSend = eveningSend;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMon1() {
        return mon1;
    }

    public void setMon1(String mon1) {
        this.mon1 = mon1;
    }

    public String getMon2() {
        return mon2;
    }

    public void setMon2(String mon2) {
        this.mon2 = mon2;
    }

    public String getMon3() {
        return mon3;
    }

    public void setMon3(String mon3) {
        this.mon3 = mon3;
    }

    public String getTue1() {
        return tue1;
    }

    public void setTue1(String tue1) {
        this.tue1 = tue1;
    }

    public String getTue2() {
        return tue2;
    }

    public void setTue2(String tue2) {
        this.tue2 = tue2;
    }

    public String getTue3() {
        return tue3;
    }

    public void setTue3(String tue3) {
        this.tue3 = tue3;
    }

    public String getWed1() {
        return wed1;
    }

    public void setWed1(String wed1) {
        this.wed1 = wed1;
    }

    public String getWed2() {
        return wed2;
    }

    public void setWed2(String wed2) {
        this.wed2 = wed2;
    }

    public String getWed3() {
        return wed3;
    }

    public void setWed3(String wed3) {
        this.wed3 = wed3;
    }

    public String getThu1() {
        return thu1;
    }

    public void setThu1(String thu1) {
        this.thu1 = thu1;
    }

    public String getThu2() {
        return thu2;
    }

    public void setThu2(String thu2) {
        this.thu2 = thu2;
    }

    public String getThu3() {
        return thu3;
    }

    public void setThu3(String thu3) {
        this.thu3 = thu3;
    }

    public String getFri1() {
        return fri1;
    }

    public void setFri1(String fri1) {
        this.fri1 = fri1;
    }

    public String getFri2() {
        return fri2;
    }

    public void setFri2(String fri2) {
        this.fri2 = fri2;
    }

    public String getFri3() {
        return fri3;
    }

    public void setFri3(String fri3) {
        this.fri3 = fri3;
    }

    public String getSat1() {
        return sat1;
    }

    public void setSat1(String sat1) {
        this.sat1 = sat1;
    }

    public String getSat2() {
        return sat2;
    }

    public void setSat2(String sat2) {
        this.sat2 = sat2;
    }

    public String getSat3() {
        return sat3;
    }

    public void setSat3(String sat3) {
        this.sat3 = sat3;
    }

    public String getSun1() {
        return sun1;
    }

    public void setSun1(String sun1) {
        this.sun1 = sun1;
    }

    public String getSun2() {
        return sun2;
    }

    public void setSun2(String sun2) {
        this.sun2 = sun2;
    }

    public String getSun3() {
        return sun3;
    }

    public void setSun3(String sun3) {
        this.sun3 = sun3;
    }

    public String getMorningSStart() {
        return morningSStart;
    }

    public void setMorningSStart(String morningSStart) {
        this.morningSStart = morningSStart;
    }

    public String getMorningSend() {
        return morningSend;
    }

    public void setMorningSend(String morningSend) {
        this.morningSend = morningSend;
    }

    public String getAfternoonSStart() {
        return afternoonSStart;
    }

    public void setAfternoonSStart(String afternoonSStart) {
        this.afternoonSStart = afternoonSStart;
    }

    public String getAfternoonSend() {
        return afternoonSend;
    }

    public void setAfternoonSend(String afternoonSend) {
        this.afternoonSend = afternoonSend;
    }

    public String getEveningSStart() {
        return eveningSStart;
    }

    public void setEveningSStart(String eveningSStart) {
        this.eveningSStart = eveningSStart;
    }

    public String getEveningSend() {
        return eveningSend;
    }

    public void setEveningSend(String eveningSend) {
        this.eveningSend = eveningSend;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }
}
