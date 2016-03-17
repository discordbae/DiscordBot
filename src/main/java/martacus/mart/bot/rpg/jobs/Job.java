package martacus.mart.bot.rpg.jobs;

public class Job {
	
	private int moneyPerMin;
	private String jobType;
	
	public Job(String job, int monpm){
		setJobType(job);
		setMoneyPerMin(monpm);
	}

	public int getMoneyPerMin() {
		return moneyPerMin;
	}

	public void setMoneyPerMin(int moneyPerMin) {
		this.moneyPerMin = moneyPerMin;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

}
