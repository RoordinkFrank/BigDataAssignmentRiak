package riakModel;


public class RiakKlantPaar {
	public RiakKlant firstKlant;
	public RiakKlant otherKlant;
	public int overeenkomstigeProducten;
	
	public RiakKlantPaar(){
		
	}
	
	public RiakKlantPaar(RiakKlant firstKlant, RiakKlant otherKlant, int overeenkomstigeProducten){
		this.firstKlant = firstKlant;
		this.otherKlant = otherKlant;
		this.overeenkomstigeProducten = overeenkomstigeProducten;
	}
	
	public void setKlantenAlfabetical(){
		int compare = firstKlant.key.compareTo(otherKlant.key);  

		if (compare < 0) {  
		    //a is smaller
		}
		else if (compare > 0) {
		    RiakKlant tempKlant = firstKlant;
		    firstKlant = otherKlant;
		    otherKlant = tempKlant;
		}
		else {  
		    //a is equal to b
		} 
	}

	@Override
	public String toString() {
		return "RiakKlantPaar [firstKlant=" + firstKlant + ", otherKlant="
				+ otherKlant + ", overeenkomstigeProducten="
				+ overeenkomstigeProducten + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstKlant == null) ? 0 : firstKlant.hashCode());
		result = prime * result
				+ ((otherKlant == null) ? 0 : otherKlant.hashCode());
		result = prime * result + overeenkomstigeProducten;
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
		RiakKlantPaar other = (RiakKlantPaar) obj;
		if (firstKlant == null) {
			if (other.firstKlant != null)
				return false;
		} else if (!firstKlant.equals(other.firstKlant))
			return false;
		if (otherKlant == null) {
			if (other.otherKlant != null)
				return false;
		} else if (!otherKlant.equals(other.otherKlant))
			return false;
		if (overeenkomstigeProducten != other.overeenkomstigeProducten)
			return false;
		return true;
	}
}
