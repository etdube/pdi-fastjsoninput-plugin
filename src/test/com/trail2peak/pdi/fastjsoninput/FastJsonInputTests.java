package com.trail2peak.pdi.fastjsoninput;

import junit.framework.TestCase;
import org.pentaho.di.TestFailedException;
import org.pentaho.di.TestUtilities;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.RowProducer;
import org.pentaho.di.trans.RowStepCollector;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by jadametz on 8/20/15.
 */
public class FastJsonInputTests extends TestCase {

    private Properties myProperties = new Properties();
    public FastJsonInputTests() {
        InputStream propertiesInputStream = getClass().getResourceAsStream("test.properties");
        try {
            this.myProperties.load(propertiesInputStream);
            propertiesInputStream.close();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("There was a problem initializing the properties file: " + e.toString());
        }
    }

    private StepMeta createFastJsonInputStep(String name, PluginRegistry registry, boolean ignoreMissingPath,
                                             boolean defaultPathLeafToNull) {
        FastJsonInputMeta fjim = new FastJsonInputMeta();
        fjim.setInFields(true);
        fjim.setFieldValue("json_data");
        fjim.setIgnoreMissingPath(ignoreMissingPath);
        fjim.setDefaultPathLeafToNull(defaultPathLeafToNull);

        FastJsonInputField if1 = new FastJsonInputField("id");
        if1.setPath("$.[*].id");
        if1.setType(ValueMeta.TYPE_INTEGER);
        if1.setTrimType(FastJsonInputField.TYPE_TRIM_NONE);

        FastJsonInputField if2 = new FastJsonInputField("first_name");
        if2.setPath("$.[*].first_name");
        if2.setType(ValueMeta.TYPE_STRING);
        if2.setTrimType(FastJsonInputField.TYPE_TRIM_NONE);

        FastJsonInputField if3 = new FastJsonInputField("last_name");
        if3.setPath("$.[*].last_name");
        if3.setType(ValueMeta.TYPE_STRING);
        if3.setTrimType(FastJsonInputField.TYPE_TRIM_NONE);

        FastJsonInputField if4 = new FastJsonInputField("city");
        if4.setPath("$.[*].city");
        if4.setType(ValueMeta.TYPE_STRING);
        if4.setTrimType(FastJsonInputField.TYPE_TRIM_NONE);

        FastJsonInputField[] inputFields = new FastJsonInputField[4];
        inputFields[0] = if1;
        inputFields[1] = if2;
        inputFields[2] = if3;
        inputFields[3] = if4;
        fjim.setInputFields(inputFields);

        String fjiPid = registry.getPluginId(StepPluginType.class, fjim);
        StepMeta fjiStep = new StepMeta(fjiPid, name, fjim);

        return fjiStep;
    }

    private StepMeta createSelectValuesStep(String name, PluginRegistry registry) {
        SelectValuesMeta svm = new SelectValuesMeta();
        String[] deleteNames = new String[1];
        deleteNames[0] = "json_data";
        svm.setDeleteName(deleteNames);

        String svPid = registry.getPluginId(StepPluginType.class, svm);
        StepMeta svStep = new StepMeta(svPid, name, svm);

        return svStep;
    }

    /**
     * Creates a row meta interface for the fields that are defined
     * @param valuesMeta defined ValueMetaInterface
     * @return RowMetaInterface
     */
    private RowMetaInterface createRowMetaInterface(ValueMetaInterface[] valuesMeta) {
        RowMetaInterface rm = new RowMeta();

        for (int i = 0; i < valuesMeta.length; i++) {
            rm.addValueMeta(valuesMeta[i]);
        }

        return rm;
    }

    /**
     * Create input data for test case 1
     * @return list of metadata/data couples
     */
    private List<RowMetaAndData> createInputData(String data) {
        List<RowMetaAndData> list = new ArrayList<RowMetaAndData>();
        ValueMetaInterface[] valuesMeta = {new ValueMeta("json_data", ValueMeta.TYPE_STRING)};
        RowMetaInterface rm = createRowMetaInterface(valuesMeta);

        Object[] r1 = new Object[] {data};

        list.add(new RowMetaAndData(rm , r1));

        return list;
    }

    /**
     * Create result data for test case 1. Each list object should mirror the output of the parsed JSON
     *
     * @return list of metadata/data couples of how the result should look.
     */
    private List<RowMetaAndData> createExpectedResults() {
        List<RowMetaAndData> list = new ArrayList<RowMetaAndData>();
        ValueMetaInterface[] valuesMeta =
                { new ValueMeta("id", ValueMeta.TYPE_INTEGER), new ValueMeta("first_name", ValueMeta.TYPE_STRING),
                        new ValueMeta("last_name", ValueMeta.TYPE_STRING), new ValueMeta("city", ValueMeta.TYPE_STRING)};
        RowMetaInterface rm = createRowMetaInterface(valuesMeta);

        Object[] r1 = new Object[] { "123", "Jesse", "Adametz", "Santa Barbara" };
        Object[] r2 = new Object[] { "456", "James", "Ebentier", "Santa Barbara" };

        list.add(new RowMetaAndData(rm, r1));
        list.add(new RowMetaAndData(rm, r2));

        return list;
    }

    /**
     * Runs the transformation with the below input parameters
     * @param inputData JSON string
     * @param ignoreMissingPath boolean
     * @param defaultPathLeafToNull boolean
     * @return Transformation Results
     */
    private List<RowMetaAndData> test(String inputData, boolean ignoreMissingPath, boolean defaultPathLeafToNull)
            throws Exception {
        KettleEnvironment.init();

        // Create a new transformation
        TransMeta transMeta = new TransMeta();
        transMeta.setName("testFastJsonInput");
        PluginRegistry registry = PluginRegistry.getInstance();

        // Create Injector
        String injectorStepName = "injector step";
        StepMeta injectorStep = TestUtilities.createInjectorStep(injectorStepName, registry);
        transMeta.addStep(injectorStep);

        // Create a FastJsonInput step
        String fastJsonInputName = "FastJsonInput step";
        StepMeta fastJsonInputStep = createFastJsonInputStep(fastJsonInputName, registry, ignoreMissingPath,
                defaultPathLeafToNull);
        transMeta.addStep(fastJsonInputStep);

        // TransHopMeta between injector step and FastJsonInput
        TransHopMeta injector_hop_fjis = new TransHopMeta(injectorStep, fastJsonInputStep);
        transMeta.addTransHop(injector_hop_fjis);

        // Create select values step to remove json field
        String selectValuesName = "select values";
        StepMeta selectValuesStep = createSelectValuesStep(selectValuesName, registry);
        transMeta.addStep(selectValuesStep);

        // TransHopMeta between FastJsonInput and Select Values
        TransHopMeta svs_hop_fjis = new TransHopMeta(fastJsonInputStep, selectValuesStep);
        transMeta.addTransHop(svs_hop_fjis);

        // Create a dummy step
        String dummyStepName = "dummy step";
        StepMeta dummyStep = TestUtilities.createDummyStep(dummyStepName, registry);
        transMeta.addStep(dummyStep);

        // TransHopMeta between Select Values and Dummy
        TransHopMeta fjis_hop_dummy = new TransHopMeta(selectValuesStep, dummyStep);
        transMeta.addTransHop(fjis_hop_dummy);

        // Execute the transformation
        Trans trans = new Trans(transMeta);
        trans.prepareExecution(null);

        // Create a row collector and add it to the dummy step interface
        StepInterface si = trans.getStepInterface(dummyStepName, 0);
        RowStepCollector dummyRowCollector = new RowStepCollector();
        si.addRowListener(dummyRowCollector);

        // Create a row producer
        RowProducer rowProducer = trans.addRowProducer(injectorStepName, 0);
        trans.startThreads();

        // create the rows
        List<RowMetaAndData> inputList = createInputData(inputData);
        Iterator<RowMetaAndData> it = inputList.iterator();
        while (it.hasNext()) {
            RowMetaAndData rowMetaAndData = it.next();
            rowProducer.putRow(rowMetaAndData.getRowMeta(), rowMetaAndData.getData());
        }
        rowProducer.finished();

        trans.waitUntilFinished();
        List<RowMetaAndData> transformationResults = dummyRowCollector.getRowsWritten();

        return transformationResults;
    }

    public void testWellStructuredJson() throws Exception {
        List<RowMetaAndData> transformationResults = test(myProperties.getProperty("WELL_STRUCTURED_JSON"), false, false);
        List<RowMetaAndData> expectedResults = createExpectedResults();
        try {
            TestUtilities.checkRows(transformationResults, expectedResults, 0);
        } catch(TestFailedException tfe) {
            fail(tfe.getMessage());
        }
    }

    public void testNoIdJson() throws Exception {
        List<RowMetaAndData> transformationResults = test(myProperties.getProperty("NO_ID_JSON"), true, false);
        List<RowMetaAndData> expectedResults = createExpectedResults();
        try {
            TestUtilities.checkRows(transformationResults, expectedResults, 0);
        } catch(TestFailedException tfe) {
            fail(tfe.getMessage());
        }
    }

    public void testMissingIdJson() throws Exception {
        List<RowMetaAndData> transformationResults = test(myProperties.getProperty("MISSING_ID_JSON"), false, true);
        List<RowMetaAndData> expectedResults = createExpectedResults();
        try {
            TestUtilities.checkRows(transformationResults, expectedResults, 0);
        } catch(TestFailedException tfe) {
            fail(tfe.getMessage());
        }
    }

    public void testNoIdAndMissingCityJson() throws Exception {
        List<RowMetaAndData> transformationResults = test(myProperties.getProperty("NO_ID_AND_MISSING_CITY_JSON"), true, true);
        List<RowMetaAndData> expectedResults = createExpectedResults();
        try {
            TestUtilities.checkRows(transformationResults, expectedResults, 0);
        } catch(TestFailedException tfe) {
            fail(tfe.getMessage());
        }
    }

}
