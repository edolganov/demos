package demo.model;

import demo.exception.ValidationException;
import static demo.util.Util.*;

public abstract class BaseBean {
	
	public static class Errors {
		private String error;
		
		public void add(String error){
			if(this.error == null) this.error = error;
		}
		
	}
	
	public String getErrorState(){
		Errors errors = new Errors();
		checkState(errors);
		return errors.error;
	}
	
	protected abstract void checkState(Errors errors);
	
	public static void checkForText(String val, String valName, Errors errors){
		if( ! hasText(val)) errors.add("empty field '"+valName+"'");
	}
	
	public static void checkForEmpty(Object val, String valName, Errors errors){
		if( isEmpty(val)) errors.add("empty field '"+valName+"'");
	}
	
	public static void checkForValid(boolean val, String valName, Errors errors){
		if( ! val) errors.add("invalid field '"+valName+"'");
	}
	
	public static void checkForInvalidChars(String val, String valName, String invalidChars, Errors errors){
		int invalidCharIndex = -1;
		int length = val.length();
		for (int i = 0; i < length; i++) {
			char c = val.charAt(i);
			invalidCharIndex = invalidChars.indexOf(c);
			if(invalidCharIndex > -1) break;
		}
		if(invalidCharIndex > -1){
			char invalidChar = invalidChars.charAt(invalidCharIndex);
			errors.add(valName+" can't contains '"+invalidChar+"'");
		}
	}
	
	public static String getErrorState(BaseBean obj){
		if(obj == null) return "null object";
		return obj.getErrorState();
	}
	
	public static void validateState(BaseBean obj){
		String error = getErrorState(obj);
		if(error != null) throw new ValidationException(error);
	}
	
	public static void validateForText(String str, String obName) {
		if(!hasText(str)) throw new ValidationException(obName+ " is empty");
	}

	public static void validateForEmpty(Object ob, String obName) {
		if(isEmpty(ob)) throw new ValidationException(obName+ " is empty");
	}
	

}
