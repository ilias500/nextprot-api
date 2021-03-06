package org.nextprot.api.core.domain;

import org.nextprot.api.commons.utils.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Try to be a generic specificity for annotations, antibodies and peptides
 * @author mpereira
 *
 */
@Deprecated
public class IsoformSpecificity implements Serializable, Comparable<IsoformSpecificity>{

	private static final long serialVersionUID = -6617265777393722080L;

	@Deprecated
	private String deprecatedIsoformName;
	private String isoformMainName;
	private String isoformAc;
	
	public void setIsoformAc(String isoformAc) {
		this.isoformAc = isoformAc;
	}


	private List<Pair<Integer, Integer>> positions;

	/**
	 * 
	 * @param isoformName should be Iso 1
	 * @param isoformAc should be NX_Q5VYM1-1
	 */
	public IsoformSpecificity(String isoformName, String isoformAc) {
		this.isoformMainName = isoformName;
		this.isoformAc = isoformAc;
	}

	//isoform name should be replaced with ac
	@Deprecated
	public IsoformSpecificity(String isoformName) {
		this.deprecatedIsoformName = isoformName;
	}

	public void setIsoformMainName(String isoformMainName) {
		this.isoformMainName = isoformMainName;
	}
	
	public String getIsoformMainName() {
		return isoformMainName;
	}

	public String getIsoformAc() {
		return isoformAc;
	}

	@Deprecated
	public String getIsoformName() {
		return deprecatedIsoformName;
	}

	@Deprecated
	public void setIsoformName(String isoformName) {
		this.deprecatedIsoformName = isoformName;
	}
	
	public List<Pair<Integer, Integer>> getPositions() {
		return positions;
	}
	
	public void setPositions(List<Pair<Integer, Integer>> positions) {
		this.positions = positions;
	}
	
	public void addPosition(int startPosition, int endPosition) {
		addPosition(Pair.create(startPosition, endPosition));
	}
	
	public void addPosition(Pair<Integer, Integer> position) {
		if(this.positions == null)
			this.positions = new ArrayList<>();
		this.positions.add(position);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("specificity for isoform "+ deprecatedIsoformName + ": ");
		boolean afterFirst = false;
		for (Pair<Integer,Integer> pos : positions) {
			if (afterFirst) sb.append(" , ");
			sb.append(pos.getFirst() + " -> " + pos.getSecond()); 			
			afterFirst=true;
		}
		return sb.toString();
	}
	
	@Override
	public int compareTo(IsoformSpecificity o) {
		String sn1 = buildSortableNameFromMainName(this.getIsoformMainName());
		String sn2 = buildSortableNameFromMainName(o.getIsoformMainName());
		return sn1.compareTo(sn2);
	}

	static String buildSortableNameFromMainName(String name) {
		if (name==null) name="";
		String sortableName = name; // by default: same as name
		if (name.startsWith("Iso ")) {
			String nb = name.substring(4);
			try {
				Integer.parseInt(nb);
				while (nb.length()<3) nb = "0" + nb;
				sortableName = "Iso "+ nb;
			} 
			catch (Exception e) { }
		} 
		return sortableName;
	}

}
