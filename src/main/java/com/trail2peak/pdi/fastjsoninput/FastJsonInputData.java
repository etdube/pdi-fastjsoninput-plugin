package com.trail2peak.pdi.fastjsoninput;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * @author Samatar
 * @author edube
 * @since 2015-01-07
 */
public class FastJsonInputData extends BaseStepData implements StepDataInterface {
  public Object[] previousRow;
  public RowMetaInterface inputRowMeta;
  public RowMetaInterface outputRowMeta;
  public RowMetaInterface convertRowMeta;
  public int nr_repeats;

  public int nrInputFields;
  public int recordnr;
  public int nrrecords;
  public Object[] readrow;
  public int totalpreviousfields;

  /**
   * The XML files to read
   */
  public FileInputList files;

  public FileObject file;
  public int filenr;

  public FileInputStream fr;
  public BufferedInputStream is;
  public String itemElement;
  public int itemCount;
  public int itemPosition;
  public long rownr;
  public int indexSourceField;

  RowMetaInterface outputMeta;

  public String filename;
  public String shortFilename;
  public String path;
  public String extension;
  public boolean hidden;
  public Date lastModificationDateTime;
  public String uriName;
  public String rootUriName;
  public long size;

  public FastJsonReader jsonReader;
  public List<JsonResultList> resultList;

  public String stringToParse;

  public FastJsonInputData() {
    super();
    nr_repeats = 0;
    previousRow = null;
    filenr = 0;

    fr = null;
    is = null;
    indexSourceField = -1;

    nrInputFields = -1;
    recordnr = 0;
    nrrecords = 0;

    readrow = null;
    totalpreviousfields = 0;
  }

}

