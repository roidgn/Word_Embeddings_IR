import java.util.Comparator;

public class RankingComparator implements Comparator<Ranking> {

	@Override
	public int compare(Ranking r1, Ranking r2) {
		
		if(r1.getValue() < r2.getValue()){
			return -1;
		}
		else if(r1.getValue() > r2.getValue()){
			return 1;
		}
		else
			return 0;
		
	}

}
