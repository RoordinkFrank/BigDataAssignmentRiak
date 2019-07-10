package riakModel;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RiakAankoop {

	public String key;
	public int aantal;
    public Date datum;

	//desiredProductFields
    public String productKey;
    
    //desiredFiliaalFields
    public String filiaalKey;
    
    //desiredKlantFields
    public String klantKey;
    
    public RiakAankoop(){
    	
    }
    
    public RiakAankoop(String key, int aantal, Date datum,
			String productKey, String filiaalKey, String klantKey) {
		super();
		this.key = key;
		this.aantal = aantal;
		this.datum = datum;
		this.productKey = productKey;
		this.filiaalKey = filiaalKey;
		this.klantKey = klantKey;
	}
    
    //Needed for ConvertAankopenToAankoopKeys(List<RiakAankoop> riakAankopen) in RiakUtil as off 11oct2018
    public String getKey(){
    	return this.key;
    }
    
    public static Comparator<RiakAankoop> COMPARE_BY_PRODUCT = new Comparator<RiakAankoop>() {
        public int compare(RiakAankoop one, RiakAankoop other) {
            System.out.println(one.productKey+"  "+other.productKey);
            return one.productKey.compareTo(other.productKey);
        }
    };

	@Override
	public String toString() {
		return "RiakAankoop [key=" + key + ", aantal=" + aantal + ", datum="
				+ datum + ", productKey=" + productKey + ", filiaalKey="
				+ filiaalKey + ", klantKey=" + klantKey + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aantal;
		result = prime * result + ((datum == null) ? 0 : datum.hashCode());
		result = prime * result
				+ ((filiaalKey == null) ? 0 : filiaalKey.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((klantKey == null) ? 0 : klantKey.hashCode());
		result = prime * result
				+ ((productKey == null) ? 0 : productKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RiakAankoop other = (RiakAankoop) obj;
		if (aantal != other.aantal)
			return false;
		if (datum == null) {
			if (other.datum != null)
				return false;
		} else if (!datum.equals(other.datum))
			return false;
		if (filiaalKey == null) {
			if (other.filiaalKey != null)
				return false;
		} else if (!filiaalKey.equals(other.filiaalKey))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (klantKey == null) {
			if (other.klantKey != null)
				return false;
		} else if (!klantKey.equals(other.klantKey))
			return false;
		if (productKey == null) {
			if (other.productKey != null)
				return false;
		} else if (!productKey.equals(other.productKey))
			return false;
		return true;
	}
}
