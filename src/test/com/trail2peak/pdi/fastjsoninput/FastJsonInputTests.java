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
import org.pentaho.di.trans.*;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jadametz on 8/20/15.
 */
public class FastJsonInputTests extends TestCase {

    private static final String WELL_STRUCTURED_JSON =
            "[{\"id\": \"123\", \"first_name\": \"Jesse\", \"last_name\": \"Adametz\", \"city\": \"Santa Barbara\"},"
                    + "{\"id\": \"456\", \"first_name\": \"James\", \"last_name\": \"Ebentier\", \"city\": \"Santa Barbara\"}]";

    private static final String MISSING_ID_JSON =
            "[{\"id\": 123, \"first_name\": \"Jesse\", \"last_name\": \"Adametz\", \"city\": \"Santa Barbara\"},"
                    + "{\"id\": 456, \"first_name\": \"James\", \"last_name\": \"Ebentier\", \"city\": \"Santa Barbara\"}]";

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
    public RowMetaInterface createRowMetaInterface(ValueMetaInterface[] valuesMeta) {
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
    public List<RowMetaAndData> createInputData() {
        List<RowMetaAndData> list = new ArrayList<RowMetaAndData>();
        ValueMetaInterface[] valuesMeta = {new ValueMeta("json_data", ValueMeta.TYPE_STRING)};
        RowMetaInterface rm = createRowMetaInterface(valuesMeta);

        Object[] r1 = new Object[] {WELL_STRUCTURED_JSON};

        list.add(new RowMetaAndData(rm , r1));

        return list;
    }

    /**
     * Create result data for test case 1. Each list object should mirror the output of the parsed JSON
     *
     * @return list of metadata/data couples of how the result should look.
     */
    public List<RowMetaAndData> createExpectedResults() {
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

    public void testParseWellStructuredJson() throws Exception {
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
        StepMeta fastJsonInputStep = createFastJsonInputStep(fastJsonInputName, registry, false, false);
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
        List<RowMetaAndData> inputList = createInputData();
        Iterator<RowMetaAndData> it = inputList.iterator();
        while (it.hasNext()) {
            RowMetaAndData rowMetaAndData = it.next();
            rowProducer.putRow(rowMetaAndData.getRowMeta(), rowMetaAndData.getData());
        }
        rowProducer.finished();

        trans.waitUntilFinished();

        // Compare the results
        List<RowMetaAndData> transformationResults = dummyRowCollector.getRowsWritten();
        List<RowMetaAndData> expectedResults = createExpectedResults();
        try {
            TestUtilities.checkRows(transformationResults, expectedResults, 0);
        } catch(TestFailedException tfe) {
            fail(tfe.getMessage());
        }
    }

}
