package com.staf.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.collections.CollectionUtils;

import com.staf.common.*;
import com.staf.reader.ReportReader;

public class Actions  {
	private static int count = 0;
	static WebDriverWait wait = new WebDriverWait(Browser.driver, 15);
	protected static WebElement action(UIObject obj) {
		WebElement element = null;
	if (obj != null){
		try{
		if(obj.getIdentifier().equalsIgnoreCase("Byid")){
			element =   Browser.driver.findElement(By.id(obj.getId()));
		}else if(obj.getIdentifier().equalsIgnoreCase("Byname")){
			element = Browser.driver.findElement(By.name(obj.getName()));
		}else if(obj.getIdentifier().equalsIgnoreCase("Byxpath")){
			element = wait.until(ExpectedConditions.visibilityOf(Browser.driver.findElement(By.xpath(obj.getXpath()))));
			//element = Browser.driver.findElement(By.xpath(obj.getXpath()));
		}else if(obj.getIdentifier().equalsIgnoreCase("BycssSelector")){
				element = Browser.driver.findElement(By.cssSelector(obj.getCssselector()));
		}else if(obj.getIdentifier().equalsIgnoreCase("Bypartiallinktext")){
				element = Browser.driver.findElement(By.partialLinkText(obj.getText()));
		}else if(obj.getIdentifier().equalsIgnoreCase("Bylinktext")){
				element = Browser.driver.findElement(By.linkText(obj.getText()));
		}else if(obj.getIdentifier().equalsIgnoreCase("Byindex")){
			List <WebElement> elements = Browser.driver.findElements(By.xpath(obj.getXpath()));
			if(elements.size()==0){
				ReportReader.report("fail", obj.getObjectName()+" Object not found");
				Assert.fail(obj.getObjectName()+" Object not found");
			}
			
			element = wait.until(ExpectedConditions.visibilityOf(elements.get(Integer.parseInt(obj.getIndex()))));
			
		}else if(obj.getIdentifier().equalsIgnoreCase("Bytagname")){
			if(obj.getType().equalsIgnoreCase("radio") || obj.getType().equalsIgnoreCase("checkbox")){
					element = Browser.driver.findElement(By.xpath("//input[@type=" + obj.getType() + "']"));
			}else{
				element = Browser.driver.findElement(By.tagName(obj.getType()));
			}
		
		}}catch(Exception ex){
			ReportReader.report("fail","Error occured with  "+ obj.getObjectName()+" and the error: "+ex.getMessage());
			Assert.fail("Error occured with  "+ obj.getObjectName());
		}}else{
			ReportReader.report("fail","Something wrong with XML. Object name is missing");
			Assert.fail("Something wrong with XML. Object name is missing");
		}

		return element;
	}
	
	
	
	

	// get the count of objects.
	public static int getCount(UIObject obj) {
		WebElement element = action(obj);
		if (element!=null){
			return count;
		}
		return 0;
	}

	//Clicks an object
	public static void click(UIObject obj) {
		WebElement element = action(obj);
	
		if(element!=null){
			try {
				element.click();
				
				ReportReader.logInfo("Clicked "+obj.getObjectName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ReportReader.report("fail","Unable to Click "+ obj.getObjectName()+" and the error "+e.getMessage());
			}
		}
	}
	
	public static void click(WebElement element) {
		if(element!=null){
			try {
				  element.click();
				ReportReader.logInfo("Clicked "+element.getText());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ReportReader.report("fail","Unable to Click "+ element.getText()+" and the error "+e.getMessage());
			}
		}
	}
	
	// Verify visibility of an object
	public static boolean isVisible(UIObject obj){
		WebElement element = action(obj);
		boolean visible = false;
		if(element!=null){
			visible = element.isDisplayed();
			ReportReader.logInfo("Returns " + obj.getObjectName() + "visibility and the visibility is "+visible);
		}
		return visible;
	}
	
	// returns object is enabledty
	public static boolean isEnabled(UIObject obj){
		WebElement element = action(obj);
		boolean enabled = false;
		if(element!=null){
			enabled = element.isEnabled();
		}
		return enabled;
	}
	
	// returns list of Elements 
	public static List<WebElement> getElements(UIObject obj){
		List<WebElement> elements = new ArrayList<WebElement>();
		boolean chkId = false;
		boolean chkClassname = false;
		boolean chkName = false;
		
		int j = 0;
		int i = 0;
		String xpa = "";
		List<WebElement> finalElements = new ArrayList<WebElement>();
		
		//Change type when Radio or Checkbox is provided as input
		if(obj.getType().equalsIgnoreCase("checkbox")||obj.getType().equalsIgnoreCase("Radio")){
			xpa = "//input[";
		}else{
			xpa = "//"+obj.getType()+"[";
		}

		try{
			if(obj.getIdentifier().equalsIgnoreCase("Byindex")){
				//when no attributes are provided except Type
				if(obj.getXpath().isEmpty() && obj.getClassname().isEmpty() && obj.getId().isEmpty() && obj.getCssselector().isEmpty() 
					&& obj.getName().isEmpty() && obj.getText().isEmpty()){
					//Verifying whether type is radio or Checkbox if so get the elements with input tag
					if(obj.getType().equalsIgnoreCase("radio")||obj.getType().equalsIgnoreCase("checkbox")){
						//Radio boxes retrieval
						if(obj.getType().equalsIgnoreCase("radio")){
							elements = Browser.driver.findElements(By.xpath("//input[@type='radio']"));
						}else if(obj.getType().equalsIgnoreCase("checkbox")){ //Checkboxes retrieval
							elements = Browser.driver.findElements(By.xpath("//input[@type='checkbox']"));
						}
						
					}else{
						elements = Browser.driver.findElements(By.tagName(obj.getType()));
					}
					
					return elements;
				}
				
				// When xpath Or CSSSelector is provided. When any of these identifiers given then other identifiers are ignored.
				if(obj.getXpath().isEmpty()==false || obj.getCssselector().isEmpty()== false){
					
					if(obj.getXpath().isEmpty()==false){
						elements = Browser.driver.findElements(By.xpath(obj.getXpath()));
					}
					if(obj.getCssselector().isEmpty()==false){
						elements = Browser.driver.findElements(By.xpath(obj.getXpath()));
					}
					return elements;
				}
				
				// When Text, XPath and CssSelector are not given and any other identifier is provided
				if((obj.getText().isEmpty()) && (obj.getId().isEmpty()==false || obj.getClassname().isEmpty()== false || obj.getName().isEmpty()==false) ) {
					if(obj.getId().isEmpty()==false){
						xpa = xpa +"@id='"+obj.getId()+"' ";
						chkId = true;
					}
					if (obj.getClassname().isEmpty()==false){
						if(chkId == true){ 
							xpa = xpa + "and "; 	
						}
						xpa = xpa + "@classname ='"+obj.getClassname()+"'";
						chkClassname = true;
					}
					
					if (obj.getName().isEmpty()==false){
						if(chkClassname == true || chkId == true){ 
							xpa = xpa + "and "; 	
						}
						xpa = xpa + "@name ='"+obj.getName()+"'";
						chkName = true;
					}
					xpa = xpa +"]";
					elements = Browser.driver.findElements(By.xpath(xpa));
					return elements;
				}
				
				//When Text is given and no other identifiers are provided
				if((obj.getText().isEmpty()==false) && (obj.getId().isEmpty() && obj.getClassname().isEmpty() && obj.getName().isEmpty())){
					elements = Browser.driver.findElements(By.linkText(obj.getText()));
					return elements;
				}
				
				//When Text is provided along with any one or all identifiers
				if((obj.getText().isEmpty()==false) && (obj.getId().isEmpty()==false || obj.getClassname().isEmpty()==false || obj.getName().isEmpty()==false)){
					if(obj.getId().isEmpty()==false){
						xpa = xpa +"@id='"+obj.getId()+"' ";
						chkId = true;
					}
					if (obj.getClassname().isEmpty()==false){
						if(chkId == true){ 
							xpa = xpa + "and "; 	
						}
						xpa = xpa + "@classname ='"+obj.getClassname()+"'";
						chkClassname = true;
					}
					
					if (obj.getName().isEmpty()==false){
						if(chkClassname == true || chkId == true){ 
							xpa = xpa + "and "; 	
						}
						xpa = xpa + "@name ='"+obj.getName()+"'";
					}
					xpa = xpa +"]";
					elements = Browser.driver.findElements(By.xpath(xpa));
					
					List<WebElement> elems = Browser.driver.findElements(By.linkText(obj.getText()));
					
					if(elements.size() == elems.size()){
						return elements;
					}
					else{
						for(i=0;i<elements.size();i++){
							if(elems.get(j).getText()==elements.get(i).getText()){
								finalElements.add(elems.get(j));
								j=j+1;
							}
						}
						return finalElements;
					}
					
				}
				
			}else if(obj.getIdentifier().equalsIgnoreCase("Byid")){
				elements = Browser.driver.findElements(By.id(obj.getId()));
			}
			
			else if(obj.getIdentifier().equalsIgnoreCase("Byname")){
				elements = Browser.driver.findElements(By.name(obj.getName()));
			}
			
			else if(obj.getIdentifier().equalsIgnoreCase("Byxpath")){
				elements = Browser.driver.findElements(By.xpath(obj.getXpath()));
			}
			
			else if(obj.getIdentifier().equalsIgnoreCase("BycssSelector")){
				elements = Browser.driver.findElements(By.cssSelector(obj.getCssselector()));
			}
			
			else if(obj.getIdentifier().equalsIgnoreCase("Bypartiallinktext")){
				elements = Browser.driver.findElements(By.partialLinkText(obj.getText()));
			}
			
			else if(obj.getIdentifier().equalsIgnoreCase("Bylinktext")){
				elements = Browser.driver.findElements(By.linkText(obj.getText()));
			}
			
			else if(obj.getIdentifier().equalsIgnoreCase("Bytagname")){
				if(obj.getType().equalsIgnoreCase("radio") || obj.getType().equalsIgnoreCase("checkbox")){
					//Radio boxes retrieval
					if(obj.getType().equalsIgnoreCase("radio")){
						elements = Browser.driver.findElements(By.xpath("//input[@type='radio']"));
					}else if(obj.getType().equalsIgnoreCase("checkbox")){ //Checkboxes retrieval
						elements = Browser.driver.findElements(By.xpath("//input[@type='checkbox']"));
					}
				}else{
					elements = Browser.driver.findElements(By.tagName(obj.getType()));
				}
				
			}
			
			else if(obj.getIdentifier().equalsIgnoreCase("Byclassname")){
				elements = Browser.driver.findElements(By.className(obj.getClassname()));
			}
			return elements;
			
		}catch(Exception e){
			ReportReader.report("fail","Error found: "+e.getMessage());
		}
		
		return elements;
	}
   
}
