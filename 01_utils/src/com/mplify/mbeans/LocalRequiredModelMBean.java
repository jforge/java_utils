package com.mplify.mbeans;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A simple subclass of RequiredModelMBean, used to intercept calls to 
 * invoke() and getAttribute() in order to log. "T" is the type of the
 * managed resource.
 * 
 * The Sun JMX implementation is wired for logging through java.util.logging.
 * Check how that is done...
 * 
 * 2011.03.30 - Created based on existing code
 ******************************************************************************/

public abstract class LocalRequiredModelMBean<T> extends RequiredModelMBean implements MBeanMarker {

    private final static String CLASS = LocalRequiredModelMBean.class.getName();
    private final static Logger LOGGER_invoke = LoggerFactory.getLogger(CLASS + ".invoke");
    private final static Logger LOGGER_getAttribute = LoggerFactory.getLogger(CLASS + ".getAttribute");

    protected final static Integer ALWAYS_VISIBLE = Integer.valueOf(1); // the value for "is always visible"
    
    /** 
     * The name under which this MBean can be found; we store it here for reference
     */

    private final ObjectName mbeanName;
    
    /**
     * Locally used class for returning attributes and operations
     */

    public static class AttrAndOperInfo {
        public final ModelMBeanAttributeInfo[] attrInfo;
        public final ModelMBeanOperationInfo[] operInfo;

        public AttrAndOperInfo(List<ModelMBeanAttributeInfo> resAttr, List<ModelMBeanOperationInfo> resOper) {
            this.attrInfo = resAttr.toArray(new ModelMBeanAttributeInfo[0]);
            this.operInfo = resOper.toArray(new ModelMBeanOperationInfo[0]);
        }
    }
    
    /**
     * Constructor taking the "managed resource" and the "mbean name" 
     */

    public LocalRequiredModelMBean(T managedResource, ObjectName mbeanName) throws RuntimeOperationsException, MBeanException, InstanceNotFoundException,
            InvalidTargetObjectTypeException, SecurityException {
        Check.notNull(managedResource,"managed resource");
        Check.notNull(mbeanName,"mbean name");
        this.mbeanName = mbeanName;
        //
        // Proceed to set the appropriate values for the superclass, which then handles all the details on its own
        //
        setModelMBeanInfo(createModelMBeanInfo(managedResource, mbeanName));
        setManagedResource(managedResource, "ObjectReference");
    }

    /**
     * Get the locally stored name of the MBean
     */

    public ObjectName getMBeanName() {
        return mbeanName;
    }
    
    /**
     * Intercept the JMX framework's call to "invoke(operation)" in order to log what's going on.
     * The JMX framework also allow java.util.logging; check how to do that
     */

    @Override
    public Object invoke(String opName, Object[] opArgs, String[] sig) throws MBeanException, ReflectionException {
        Logger logger = LOGGER_invoke;
        if (logger.isDebugEnabled()) {
            logger.debug("invoke(" + opName + ")");
        }
        try {
            // >>>>>>>
            Object res = super.invoke(opName, opArgs, sig);
            // <<<<<<<
            if (logger.isDebugEnabled()) {
                logger.debug("invoke(" + opName + ") returned " + res);
            }
            return res;
        } catch (MBeanException exe) {
            if (logger.isDebugEnabled()) {
                logger.debug("invoke(" + opName + ") throws MBeanException", exe);
            }
            throw exe;
        }
    }

    /**
     * Intercept the JMX framework's call to "getAttribute(attrName)" in order to log what's going on.
     * The JMX framework also allow java.util.logging; check how to do that 
     */

    @Override
    public Object getAttribute(String attrName) throws MBeanException, ReflectionException, AttributeNotFoundException {
        Logger logger = LOGGER_getAttribute;
        if (logger.isDebugEnabled()) {
            logger.debug("getAttribute(" + attrName + ")");
        }
        try {
            if (logger.isDebugEnabled()) {
                //
                // This code is run by super.getAttribute at some time; we want to check what id does here...
                //
                ModelMBeanAttributeInfo mmAttrInfo = ((ModelMBeanInfo) getMBeanInfo()).getAttribute(attrName);
                Descriptor attrDescr = mmAttrInfo.getDescriptor();
                String attrGetMethod = (String) (attrDescr.getFieldValue("getMethod"));
                if (attrGetMethod == null) {
                    logger.debug("There is no 'getMethod' registered for attribute '" + attrName + "'");
                } else {
                    logger.debug("'getMethod' registered for attribute '" + attrName + "' is '" + attrGetMethod + "'");
                }
            }
            // >>>>>>>>>
            Object res = super.getAttribute(attrName);
            // <<<<<<<<<
            if (logger.isDebugEnabled()) {
                logger.debug("getAttribute(" + attrName + ") returned " + res);
            }
            return res;
        } catch (MBeanException exe) {
            if (logger.isDebugEnabled()) {
                logger.debug("getAttribute(" + attrName + ") throws MBeanException", exe);
            }
            throw exe;
        } catch (AttributeNotFoundException exe) {
            if (logger.isDebugEnabled()) {
                logger.debug("getAttribute(" + attrName + ") throws AttributeNotFoundException", exe);
            }
            throw exe;
        }
    }
    
    /**
     * Helper for remapping a Map into a DescriptorSupport
     */

    protected static DescriptorSupport createDescriptorSupport(Map<String, Object> map) {
        String[] fieldNames = new String[map.size()];
        Object[] fieldValues = new Object[map.size()];
        int i = 0;
        for (Entry<String, Object> e : map.entrySet()) {
            fieldNames[i] = e.getKey();
            fieldValues[i] = e.getValue();
            i++;
        }
        return new DescriptorSupport(fieldNames, fieldValues);
    }
    
    /**
     * To be implemented by subclasses: creation of the ModelMBeanInfo
     */
    
    protected abstract ModelMBeanInfo createModelMBeanInfo(T managedResource, ObjectName mbeanName);
    

}
