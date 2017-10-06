package in.arjsna.permissionchecker.appdetails;

public class AppUninstallEvent {
  public int positionOfAppInList;

  public AppUninstallEvent(int positionInList) {
    this.positionOfAppInList = positionInList;
  }
}
