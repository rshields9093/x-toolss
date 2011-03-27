


import lib.genevot.Interval;


public class MemespaceInformation {
	private String username;
	private String password;
	private String migrationPassword;
	private String creator;
	private String dateCreated;
	private String problemDescription;
	private Interval[] bounds;
	private int id;
	
	public MemespaceInformation(String username, String password, String migrationPwd, String creator, String date, String problem, Interval[] bounds) {
		this.username = username;
		this.password = password;
		this.migrationPassword = migrationPwd;
		this.bounds = bounds;
		this.creator = creator.replace(';', '-').replace('\r', '-').replace('\n', '-');
		dateCreated = date.replace(';', '-').replace('\r', '-').replace('\n', '-');
		problemDescription = problem.replace(';', '-').replace('\r', '-').replace('\n', '-');
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getMigrationPassword() {
		return migrationPassword;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public String getDateCreated() {
		return dateCreated;
	}	
	
	public String getProblemDescription() {
		return problemDescription;
	}	
	
	public Interval[] getBounds() {
		return (Interval[])bounds.clone();
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
}
