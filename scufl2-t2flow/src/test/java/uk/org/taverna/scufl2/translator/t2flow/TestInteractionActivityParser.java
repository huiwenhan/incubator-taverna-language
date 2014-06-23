package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;

import java.net.URL;

import javax.xml.bind.JAXBException;

import static uk.org.taverna.scufl2.translator.t2flow.defaultactivities.InteractionActivityParser.ACTIVITY_URI;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestInteractionActivityParser {
	
	private static final String WF_SIMPLE_TELL = "/interaction_simple_tell.t2flow";
	private static final String WF_MULTIPLE_CHOICE = "/interaction_multiple_choice.t2flow";
	
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	
	private WorkflowBundle parseWorkflow(final String wfPath) throws Exception {
		URL wfResource = getClass().getResource(wfPath);
		assertNotNull("Could not find workflow " + wfPath, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle researchObj = parser
				.parseT2Flow(wfResource.openStream());
		
		return researchObj;
	}

	@Test
	public void parseSimpleTell() throws Exception {
		WorkflowBundle researchObj = parseWorkflow(WF_SIMPLE_TELL);
		
		NamedSet<Profile> profiles = researchObj.getProfiles();
		Profile profile = profiles.getByName("taverna-biodiversity-2.5.0");
		assertNotNull("Could not find profile", profile);
		
		Processor tell = researchObj.getMainWorkflow().getProcessors()
				.getByName("tell");
		assertNotNull("Could not find processor tell", tell);
		
		Configuration tellConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(tell, profile);
		
		Activity tellAct = (Activity) tellConfig.getConfigures();
		assertEquals(ACTIVITY_URI, tellAct.getType());
		
		ObjectNode tellResource = tellConfig.getJsonAsObjectNode();
		assertEquals(ACTIVITY_URI.resolve("#Config"), tellConfig.getType());
		
		String presentationOrigin = tellResource.get("presentationOrigin").textValue();
		assertEquals("tell", presentationOrigin);
		
		String interactionActivityType = tellResource.get("interactionActivityType").textValue();
		assertEquals("VelocityTemplate", interactionActivityType);
		
		boolean progressNotification = tellResource.get("progressNotification").booleanValue();
		assertFalse(progressNotification);
	}
	
	@Test
	public void parseMultipleChoice() throws Exception {
		WorkflowBundle researchObj = parseWorkflow(WF_MULTIPLE_CHOICE);
		
		NamedSet<Profile> profiles = researchObj.getProfiles();
		Profile profile = profiles.getByName("taverna-biodiversity-2.5.0");
		assertNotNull("Could not find profile", profile);
		
		Processor interaction = researchObj.getMainWorkflow().getProcessors()
				.getByName("Interaction");
		assertNotNull("Could not find processor Interaction", interaction);
		
		Configuration interactionConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(interaction, profile);
		
		Activity interactionAct = (Activity) interactionConfig.getConfigures();
		assertEquals(ACTIVITY_URI, interactionAct.getType());
		
		ObjectNode interactionResource = interactionConfig.getJsonAsObjectNode();
		assertEquals(ACTIVITY_URI.resolve("#Config"), interactionConfig.getType());
		
		String presentationOrigin = interactionResource.get("presentationOrigin").textValue();
		assertEquals("http://build.mygrid.org.uk/taverna/internal/biovel/multiple_selection.html", presentationOrigin);
		
		String interactionActivityType = interactionResource.get("interactionActivityType").textValue();
		assertEquals("LocallyPresentedHtml", interactionActivityType);
		
		boolean progressNotification = interactionResource.get("progressNotification").booleanValue();
		assertFalse(progressNotification);		
	}

}
