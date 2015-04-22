package gov.va.semoss.ui.components.insight.manager;

import gov.va.semoss.om.Insight;
import gov.va.semoss.om.Parameter;
import gov.va.semoss.om.Perspective;
import gov.va.semoss.util.Utility;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.scene.control.Tooltip;

public class ParameterTabController extends InsightManagerController {
	private InsightManagerController imc;

	/**   Constructor: Sets a local object of InsightManagerController, 
	 * defines tool-tips, and declares handlers for all buttons on the 
	 * "Parameter" tab.
	 * 
	 * @param imc -- (InsightManagerController) Reference to an object of the parent class.
	 */
	public ParameterTabController(InsightManagerController imc){
		this.imc = imc;
		this.imc.btnSaveParameter_Parm.setOnAction(this::handleSaveParameter);
		this.imc.btnSaveParameter_Parm.setTooltip(
				new Tooltip("Save all Parameter fields."));
		//Note: the "Reload" handler has been defined once for all in "PerspectiveTabController":
		this.imc.btnReloadParameter_Parm.setOnAction(this.imc.ptc::handleReloadPerspectives);
		this.imc.btnReloadParameter_Parm.setTooltip(new Tooltip(this.imc.btnReloadPerspective.getTooltip().getText()));
	}
	
	/**   Click-handler for the "Save Parameter" button. Saves changes to all fields on the "Parameter" tab.
	 * 
	 * @param event
	 */
	private void handleSaveParameter(ActionEvent event){
		Perspective perspective = imc.arylPerspectives.get(imc.intCurPerspectiveIndex);
		Insight insight = imc.arylInsights.get(imc.intCurInsightIndex);
		Parameter parameter = imc.arylInsightParameters.get(imc.intCurParameterIndex);
		
		parameter.setLabel(imc.legalizeQuotes(imc.txtLabel_parm.getText().trim()));
		parameter.setVariable(imc.legalizeQuotes(imc.txtVariable_parm.getText().trim()));
		parameter.setValueType(imc.legalizeQuotes(imc.txtValueType_parm.getText().trim()));
		parameter.setDefaultValue(imc.legalizeQuotes(imc.txtDefaultValue_parm.getText().trim()));
		parameter.setDefaultQuery(imc.legalizeQuotes(imc.txtaDefaultQuery_parm.getText().trim()));
		
		//Define a Task to save the current Parameter:
		Task<Boolean> saveParameter = new Task<Boolean>(){
		   @Override 
		   protected Boolean call() throws Exception{
		      return imc.engine.getWriteableInsightManager().getWriteableParameterTab().saveParameter(insight, parameter);
		   }
		};
        //Define a listener to update the JavaFX UI when the Task completes:
		saveParameter.stateProperty().addListener(new ChangeListener<Worker.State>(){
           @Override 
           public void changed(ObservableValue<? extends Worker.State> observableValue, 
        	  Worker.State oldState, Worker.State newState){
              if(newState == Worker.State.SUCCEEDED){
            	 if(saveParameter.getValue() == true){
          		    Utility.showMessage("Parameter fields saved ok.");
    	 	        //Reload the UI from the database:
          		    imc.loadData(imc.txtPerspectiveTitle.getText().trim(), 
          		       insight.getOrderedLabel(perspective.getUri()), parameter.getLabel());
            	 }else{
            		Utility.showError("Error saving Parameter.");
            	 }          		 
              }else if(newState == Worker.State.FAILED){
            	 Utility.showError("Error saving Parameter. Operation rolled back.");
              }
           }
        });
		//Run the Task on a separate Thread:
		new Thread(saveParameter).start();
	}

}//End "ParameterTabController" class.