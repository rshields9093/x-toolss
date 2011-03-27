import lib.genevot.Interval;
import lib.genevot.Chromosome;
import lib.genevot.Individual;
import java.util.StringTokenizer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormatSymbols;


// Memespace Protocol
//
// Incoming:						            																					Outgoing:
// CREATE; #SIZE; $USERNAME; $PASSWORD; 1; $MIGRATIONPASSWORD; $CREATOR; $PROBLEMDESCRIPTION; #NUMGENES; #TYPE; #MIN; #MAX;...				ERROR or #ID
// CREATE; #SIZE; $USERNAME; $PASSWORD; 0; $CREATOR; $PROBLEMDESCRIPTION; #NUMGENES; #TYPE; #MIN; #MAX;...									ERROR or #ID
// DESTROY; #ID; $USERNAME; $PASSWORD 																						1 or 0
// MIGRATE; #ID; 1; $MIGRATIONPASSWORD; #FITNESS; #NUMGENES; #TYPE; #MIN; #MAX; #VALUE;...												ERROR or 0 or 1; #FITNESS; #NUMGENES; #TYPE; #MIN; #MAX; #VALUE;... 
// MIGRATE; #ID; 0; #FITNESS; #NUMGENES; #TYPE; #MIN; #MAX; #VALUE;...																	ERROR or 0 or 1; #FITNESS; #NUMGENES; #TYPE; #MIN; #MAX; #VALUE;... 
// POLL																													#NUMMEMESPACES; #ID; #USEMIGRATIONPASSWORD; $CREATOR; $DATECREATED; $PROBLEMDESCRIPTION; #NUMGENES; #TYPE; #MIN; #MAX;...
// POLL; #ID; $USERNAME; $PASSWORD																							ERROR or #USEMIGRATIONPASSWORD; $CREATOR; $DATECREATED; $PROBLEMDESCRIPTION; #NUMSOLUTIONS; #FITNESS; #NUMGENES; #TYPE; #MIN; #MAX; #VALUE;... 


public class MemespaceProtocol {
	public static final int BOOLEAN = 0;
	public static final int INTEGER = 1;
	public static final int FLOAT = 2;
	public static final int DOUBLE = 3;

	private static final String GET_ALL_MEMESPACE_INFO = "POLL";
	private static final String CREATE_MEMESPACE = "CREATE";
	private static final String DESTROY_MEMESPACE = "DESTROY";
	private static final String MIGRATE = "MIGRATE";
	private static final String NO_RETURN = "0";
	private static final String YES_RETURN = "1";
	private static final String ERROR = "ERROR";
	
	private MemespaceServer memespaceServer;
	
	public MemespaceProtocol(MemespaceServer ms) {
		memespaceServer = ms;
	}

	public static Interval.Type convertToType(String s) {
		int numType = Integer.parseInt(s);
		if(numType == BOOLEAN) {
			return Interval.Type.BOOLEAN;
		}
		else if(numType == INTEGER) {
			return Interval.Type.INTEGER;
		}
		else if(numType == FLOAT) {
			return Interval.Type.FLOAT;
		}
		else {
			return Interval.Type.DOUBLE;
		}
	}
	
	public static String convertInterval(Interval interval) {
		String s = "";
		if(interval.getType() == Interval.Type.BOOLEAN) {
			s += BOOLEAN + ";";
			Boolean min = (Boolean)interval.getMin();
			Boolean max = (Boolean)interval.getMax();
			s += (min)? "1;" : "0;";
			s += (max)? "1;" : "0;";
		}
		else if(interval.getType() == Interval.Type.INTEGER) {
			s += INTEGER + ";";
			Integer min = (Integer)interval.getMin();
			Integer max = (Integer)interval.getMax();
			s += String.valueOf(min.intValue()) + ";";
			s += String.valueOf(max.intValue()) + ";";
		}
		else if(interval.getType() == Interval.Type.FLOAT) {
			s += FLOAT + ";";
			Float min = (Float)interval.getMin();
			Float max = (Float)interval.getMax();
			s += String.valueOf(min.floatValue()) + ";";
			s += String.valueOf(max.floatValue()) + ";";
		}
		else {
			s += DOUBLE + ";";
			Double min = (Double)interval.getMin();
			Double max = (Double)interval.getMax();
			s += String.valueOf(min.doubleValue()) + ";";
			s += String.valueOf(max.doubleValue()) + ";";
		}
		return s;
	}
	
	public static Interval createInterval(Interval.Type type, String min, String max) {
		Interval interval = null;
		if(type == Interval.Type.BOOLEAN) {
			boolean minVal = (Integer.parseInt(min) > 0)? true : false;
			boolean maxVal = (Integer.parseInt(max) > 0)? true : false;
			interval = new Interval(type, new Boolean(minVal), new Boolean(maxVal));
		}
		else if(type == Interval.Type.INTEGER) {
			int minVal = Integer.parseInt(min);
			int maxVal = Integer.parseInt(max);
			interval = new Interval(type, new Integer(minVal), new Integer(maxVal));
		}
		else if(type == Interval.Type.FLOAT) {
			float minVal = Float.parseFloat(min);
			float maxVal = Float.parseFloat(max);
			interval = new Interval(type, new Float(minVal), new Float(maxVal));
		}
		else {
			double minVal = Double.parseDouble(min);
			double maxVal = Double.parseDouble(max);
			interval = new Interval(type, new Double(minVal), new Double(maxVal));
		}	
		return interval;
	}
	
	public static Individual createIndividual(String str) {
		str = str.trim();
		StringTokenizer tok = new StringTokenizer(str, ";\r\n");
		double fitness = Double.parseDouble(tok.nextToken());
		int numGenes = Integer.parseInt(tok.nextToken());
		Interval[] bounds = new Interval[numGenes];
		Object[] value = new Object[numGenes];
		int i = 0;
		while(tok.hasMoreTokens() && i < numGenes) {
			int type = Integer.parseInt(tok.nextToken());
			String min = tok.nextToken();
			String max = tok.nextToken();
			String val = tok.nextToken();
			if(type == BOOLEAN) {
				int num = Integer.parseInt(min);
				Boolean tempMin = new Boolean(num != 0);
				num = Integer.parseInt(max);
				Boolean tempMax = new Boolean(num != 0);
				bounds[i] = new Interval(Interval.Type.BOOLEAN, tempMin, tempMax);
				num = Integer.parseInt(val);
				value[i] = new Boolean(num != 0);
			}
			else if(type == INTEGER) {
				int num = Integer.parseInt(min);
				Integer tempMin = new Integer(num);
				num = Integer.parseInt(max);
				Integer tempMax = new Integer(num);
				bounds[i] = new Interval(Interval.Type.INTEGER, tempMin, tempMax);
				num = Integer.parseInt(val);
				value[i] = new Integer(num);
			}
			else if(type == FLOAT) {
				float num = Float.parseFloat(min);
				Float tempMin = new Float(num);
				num = Float.parseFloat(max);
				Float tempMax = new Float(num);
				bounds[i] = new Interval(Interval.Type.FLOAT, tempMin, tempMax);
				num = Float.parseFloat(val);
				value[i] = new Float(num);
			}
			else {
				double num = Double.parseDouble(min);
				Double tempMin = new Double(num);
				num = Double.parseDouble(max);
				Double tempMax = new Double(num);
				bounds[i] = new Interval(Interval.Type.DOUBLE, tempMin, tempMax);
				num = Double.parseDouble(val);
				value[i] = new Double(num);
			}
			i++;
		}
		Chromosome c = new Chromosome(bounds);
		for(i = 0; i < numGenes; i++) {
			c.setGene(i, value[i]);
		}
		Individual newIndividual = new Individual(c);
		newIndividual.setFitness(fitness);
		return newIndividual;
	}
	
	public static String convertIndividual(Individual individual) {
		String s = "";
		s += individual.getFitness() + ";";
		int numGenes = individual.getChromosome().getSize();
		s += numGenes;
		for(int i = 0; i < numGenes; i++) {
			s += ";";
			Interval.Type type = individual.getChromosome().getBounds(i).getType();
			if(type == Interval.Type.BOOLEAN) {
				s += BOOLEAN + ";";
				Boolean min = (Boolean)individual.getChromosome().getBounds(i).getMin();
				Boolean max = (Boolean)individual.getChromosome().getBounds(i).getMax();
				Boolean value = (Boolean)individual.getChromosome().getGene(i);
				s += (min)? "1;" : "0;";
				s += (max)? "1;" : "0;";
				s += (value)? "1" : "0";
			}
			else if(type == Interval.Type.INTEGER) {
				s += INTEGER + ";";
				Integer min = (Integer)individual.getChromosome().getBounds(i).getMin();
				Integer max = (Integer)individual.getChromosome().getBounds(i).getMax();
				Integer value = (Integer)individual.getChromosome().getGene(i);
				s += String.valueOf(min.intValue()) + ";";
				s += String.valueOf(max.intValue()) + ";";
				s += String.valueOf(value.intValue());
			}
			else if(type == Interval.Type.FLOAT) {
				s += FLOAT + ";";
				Float min = (Float)individual.getChromosome().getBounds(i).getMin();
				Float max = (Float)individual.getChromosome().getBounds(i).getMax();
				Float value = (Float)individual.getChromosome().getGene(i);
				s += String.valueOf(min.floatValue()) + ";";
				s += String.valueOf(max.floatValue()) + ";";
				s += String.valueOf(value.floatValue());
			}
			else {
				s += DOUBLE + ";";
				Double min = (Double)individual.getChromosome().getBounds(i).getMin();
				Double max = (Double)individual.getChromosome().getBounds(i).getMax();
				Double value = (Double)individual.getChromosome().getGene(i);
				s += String.valueOf(min.doubleValue()) + ";";
				s += String.valueOf(max.doubleValue()) + ";";
				s += String.valueOf(value.doubleValue());
			}
		}
		return s;
	}
	
	public String processInput(String input) {
		if(input == null) {
			return null;
		}
		else {
			StringTokenizer tok = new StringTokenizer(input, ";\r\n");
			String command = tok.nextToken();
			if(command.equalsIgnoreCase(GET_ALL_MEMESPACE_INFO)) {
				if(tok.hasMoreTokens()) {
					int id = Integer.parseInt(tok.nextToken());
					String username = tok.nextToken().trim();
					String password = tok.nextToken().trim();
					Memespace ms = memespaceServer.getMemespace(id);
					if(ms != null) {
						MemespaceInformation info = ms.getInfo();
						String rootUser = memespaceServer.getRootUsername();
						String rootPwd = memespaceServer.getRootPassword();
						if((info.getUsername().equals(username) && info.getPassword().equals(password)) || 
						   (rootUser != null && rootPwd != null && username.equals(rootUser) && password.equals(rootPwd))) {
							String s = "";
  							s += (info.getMigrationPassword() != null)? "1;" : "0;";
							s += info.getCreator() + ";";
							s += info.getDateCreated() + ";";
							s += info.getProblemDescription() + ";";
							int msSize = ms.getNumIndividuals();
							s += msSize + ";";
							for(int i = 0; i < msSize; i++) {
								Individual ind = ms.getIndividual(i);
								if(ind != null) {
									s += convertIndividual(ind) + ";";
								}
								else {
									return ERROR;
								}
							}
							return s;
						}
						else {
							return ERROR;
						}
					}
					else {
						return ERROR;
					}
				}
				else {
					MemespaceInformation[] mi = memespaceServer.getAllMemespaces();
					if(mi == null) {
						return String.valueOf(0);
					}
					else {
						String s = String.valueOf(mi.length);
						for(int i = 0; i < mi.length; i++) {
							s += ";";
							s += String.valueOf(mi[i].getID()) + ";";
							s += (mi[i].getMigrationPassword() != null)? "1;" : "0;";
							s += mi[i].getCreator() + ";";
							s += mi[i].getDateCreated() + ";";
							s += mi[i].getProblemDescription() + ";";
							Interval[] bounds = mi[i].getBounds();
							s += bounds.length + ";";
							for(int j = 0; j < bounds.length; j++) {
								s += convertInterval(bounds[j]);
							}
						}
						return s;	
					}
				}	
			}
			else if(command.equalsIgnoreCase(CREATE_MEMESPACE)) {
				int size = Integer.parseInt(tok.nextToken());
				if(size > 0) {
					String username = tok.nextToken().trim();
					String password = tok.nextToken().trim();
					int useMigrationPassword = Integer.parseInt(tok.nextToken().trim());
					String migrationPassword = null;
					if(useMigrationPassword > 0) {
						migrationPassword = tok.nextToken().trim();
					}
					String creator = tok.nextToken();
					String description = tok.nextToken();
					int numGenes = Integer.parseInt(tok.nextToken().trim());
					Interval[] bounds = new Interval[numGenes];
					for(int i = 0; i < bounds.length; i++) {
						Interval.Type type = convertToType(tok.nextToken().trim());
						String min = tok.nextToken().trim();
						String max = tok.nextToken().trim();
						bounds[i] = createInterval(type, min, max);						
					}
					GregorianCalendar now = new GregorianCalendar();
					String month = (new DateFormatSymbols()).getMonths()[now.get(Calendar.MONTH)];
					String date = now.get(Calendar.DATE) + "-" + month + "-" + now.get(Calendar.YEAR);
					int id = memespaceServer.addMemespace(size, new MemespaceInformation(username, password, migrationPassword, creator, date, description, bounds));
					return String.valueOf(id);
				}
				else {
					return ERROR;
				}					
			}
			else if(command.equalsIgnoreCase(DESTROY_MEMESPACE)) {
				int id = Integer.parseInt(tok.nextToken());
				String username = tok.nextToken().trim();
				String password = tok.nextToken().trim();
				Memespace ms = memespaceServer.getMemespace(id);
				if(ms != null) {
					MemespaceInformation info = ms.getInfo();
					String rootUser = memespaceServer.getRootUsername();
					String rootPwd = memespaceServer.getRootPassword();
					if((info.getUsername().equals(username) && info.getPassword().equals(password)) || 
					   (rootUser != null && rootPwd != null && username.equals(rootUser) && password.equals(rootPwd))) {
						boolean wasRemoved = memespaceServer.removeMemespace(id);
						if(wasRemoved) {
							return "1";
						}
						else {
							return "0";
						}
					}
					else {
						return "0";
					}
				}
				else {
					return "0";
				}
			}
			else if(command.equalsIgnoreCase(MIGRATE)) {
				int id = Integer.parseInt(tok.nextToken());
				int useMigrationPassword = Integer.parseInt(tok.nextToken());
				String migrationPassword = null;
				if(useMigrationPassword > 0) {
					migrationPassword = tok.nextToken().trim();
				}
				Memespace m = memespaceServer.getMemespace(id);
				if(m == null || (m.getInfo().getMigrationPassword() != null && !m.getInfo().getMigrationPassword().equals(migrationPassword))) {
					return ERROR;
				}
				
				String indStr = "";
				while(tok.hasMoreTokens()) {
					indStr += tok.nextToken() + ";";
				}
				
				Individual newIndividual = createIndividual(indStr);
				System.out.println("Receiving: " + newIndividual.getChromosome());
				Individual oldIndividual = m.add(newIndividual);
				System.out.println("Sending: " + oldIndividual.getChromosome());
				if(oldIndividual == null) {
					return ERROR;
				}
				else {
					return YES_RETURN + ";" + convertIndividual(oldIndividual);
				}
			}
			else {
				return null;
			}
		}
	}	
}


