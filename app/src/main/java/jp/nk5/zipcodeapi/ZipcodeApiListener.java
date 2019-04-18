package jp.nk5.zipcodeapi;

public interface ZipcodeApiListener {

    void lockUI();
    void unlockUI();
    void updateUI(String returnString);
}
