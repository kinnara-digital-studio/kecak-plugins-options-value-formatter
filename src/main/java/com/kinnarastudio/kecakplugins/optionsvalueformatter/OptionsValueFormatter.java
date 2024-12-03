/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kinnarastudio.kecakplugins.optionsvalueformatter;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormatDefault;
import org.joget.apps.form.model.FormBinder;
import org.joget.apps.form.model.FormLoadBinder;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.plugin.base.PluginManager;

import java.util.*;

/**
 *
 * @author Yonathan
 */
public class OptionsValueFormatter extends DataListColumnFormatDefault {

    Map<String, String> optionMap = null;

    @Override
    public String getName() {
        return "Options Value Formatter";
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getDescription() {
        return "Format column value based on options in form; Artifact ID : " + getClass().getPackage().getImplementationTitle();
    }

    @Override
    public String format(DataList dataList, DataListColumn column, Object row, Object value) {
        boolean emptyIfNotFound = "true".equalsIgnoreCase(getPropertyString("emptyIfNotFound"));
        String result = "";
        if (value != null) {
            String[] values = value.toString().split(";");
            ArrayList<String> results = new ArrayList<String>();
            for (String v : values) {
                if (this.getOptionMap().containsKey(v)) {
                    results.add(this.getOptionMap().get(v));
                    continue;
                }
                results.add(emptyIfNotFound ? "" : v);
            }
            result = this.join(results, (String) ", ");
        }
        return result;
    }

    public static String join(List<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for (String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }

    public String getLabel() {
        return "Option Value Formatter";
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource((String) this.getClass().getName(), (String) "/properties/optionValueFormatter.json", (Object[]) null, true, "messages/optionValueFormatter");
    }

    protected Map<String, String> getOptionMap() {
        FormBinder optionBinder;
        PluginManager pluginManager;
        Object[] options;
        if (this.optionMap != null) {
            return this.optionMap;
        }
        this.optionMap = new HashMap();
        for (Object o : options = (Object[]) this.getProperty("options")) {
            HashMap option = (HashMap) o;
            Object value = option.get("value");
            Object label = option.get("label");
            if (value == null || label == null) {
                continue;
            }
            this.optionMap.put(value.toString(), label.toString());
        }
        Map optionsBinderProperties = (Map) this.getProperty("optionsBinder");
        if (optionsBinderProperties != null && optionsBinderProperties.get("className") != null && !optionsBinderProperties.get("className").toString().isEmpty() && (optionBinder = (FormBinder) (pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager")).getPlugin(optionsBinderProperties.get("className").toString())) != null) {
            optionBinder.setProperties((Map) optionsBinderProperties.get("properties"));
            FormRowSet rowSet = ((FormLoadBinder) optionBinder).load(null, null, null);
            if (rowSet != null) {
                this.optionMap = new HashMap();
                for (FormRow row : rowSet) {
                    String label;
                    Iterator it = row.stringPropertyNames().iterator();
                    String value = row.getProperty("value");
                    if (value == null) {
                        String key = (String) it.next();
                        value = row.getProperty(key);
                    }
                    if ((label = row.getProperty("label")) == null) {
                        String key = (String) it.next();
                        label = row.getProperty(key);
                    }
                    this.optionMap.put(value, label);
                }
            }
        }
        return this.optionMap;
    }

}
