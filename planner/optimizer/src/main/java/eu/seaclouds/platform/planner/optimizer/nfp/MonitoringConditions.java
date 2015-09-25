package eu.seaclouds.platform.planner.optimizer.nfp;

public class MonitoringConditions extends QualityInformation {

   public MonitoringConditions() {
      // TODO Auto-generated constructor stub
   }

   private String moduleName;

   public String getModuleName() {
      return moduleName;
   }

   public void setModuleName(String moduleNane) {
      this.moduleName = moduleNane;
   }
   
   
  @Override
  public String toString(){
     return "moduleName=" + moduleName + " RespTime=" + super.getResponseTime() + " Availability="
           + super.getAvailability() ;
  }
}
