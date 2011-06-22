class Variable{
	private String displayName;
	private String value;
	private boolean isArray, isAlias, isConstant;
	private String constantValue;
	private int aliasMod, aliasVar;
	private int arrayLoc;
	private int type; // 1 input, 0 output, -1 not set
	//input vars only
	private String lowerRange, upperRange, valueType;

	//**************************************************
	// initialization methods
	//**************************************************
	public Variable(){//new module
		init("");
	}

	public Variable(String name){//existing module
		init(name);  
	}

	public void init(String name){
		displayName = name;
		value = "";
		type = -1;

		lowerRange = "";
		upperRange = "";
		valueType = "";  

		isArray = false;
		arrayLoc = 0;

		isAlias = false;
		aliasMod = 0; 
		aliasVar = 0;

		isConstant = false;
		constantValue = "";
	}

	//**************************************************
	// set methods
	//**************************************************
	public void setDisplayName(String name){
		displayName = name;
	}  

	public void setValue(String val){
		value = val;
	}

	public boolean setType(int t){
		boolean isSet = false;
		if(t == 0 || t == 1){
			type = t;
			isSet = true;
		}
		return isSet;
	}

	public void setLowerRange(String lowRange){
		lowerRange = lowRange;
	}

	public void setUpperRange(String upRange){
		upperRange = upRange;
	}

	public void setValueType(String t){
		valueType = t;
	}

	public void setArrayLoc(int arrLoc, boolean isInArray){
		arrayLoc = arrLoc;
		isArray = isInArray;
	}

	public void setConstant(String con){
		constantValue = con;
		isConstant = true;
	}

	public void setConstant(boolean is){
		isConstant = is;
	}

	public void setAlias(int modNum, int varNum){
		aliasMod = modNum;
		aliasVar = varNum;
		isAlias = true;
	}

	public void setAlias(boolean is){
		isAlias = is;
	}

	//**************************************************
	// get methods
	//**************************************************
	public String getName(){
		return displayName.trim();
	}

	public String getDisplayName(){
		return displayName;
	}  

	public String getValue(){
		return value;
	}

	public int getType(){
		return type;
	}

	public String getLowerRange(){
		return lowerRange;
	}

	public String getUpperRange(){
		return upperRange;
	}

	public String getValueType(){
		return valueType;
	}

	public int getArrayLoc(){
		return arrayLoc;
	}

	public String getConstant(){
		return constantValue;
	}

	public boolean getIsConstant(){
		return isConstant;
	}

	public int getAliasMod(){
		return aliasMod;
	}

	public int getAliasVar(){
		return aliasVar;
	}

	public boolean getIsAlias(){
		return isAlias;
	}

}
