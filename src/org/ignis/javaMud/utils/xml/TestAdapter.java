package org.ignis.javaMud.utils.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.ignis.javaMud.Mud.dataholder.Test;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.dataholder.test.SkillTest;
public class TestAdapter extends XmlAdapter<TestAdapter.AdaptedTest, Test> {

    public static class AdaptedTest {
    	 
    	@XmlAttribute
    	public String type;
     	@XmlAttribute
     	public String name;
    	@XmlAttribute
    	public int difficulty;
 
    }

	@Override
	public Test unmarshal(AdaptedTest testData) throws Exception {
        if (null == testData) {
            return null;
        }
        if(testData.type.equalsIgnoreCase(Test.T_SENSE)) {
        	return new SenseTest(testData.name, testData.difficulty);
        }
        if(testData.type.equalsIgnoreCase(Test.T_SKILL)) {
        	return new SkillTest(testData.name, testData.difficulty);
        }
        return null;
    }
	

	@Override
	public AdaptedTest marshal(Test v) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}}
