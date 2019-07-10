package riakModel;

public class RiakFiliaal{
	public String key;
	
	public RiakFiliaal(){
		
	}
	
	public RiakFiliaal(String key) {
		super();
		this.key = key;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RiakFiliaal other = (RiakFiliaal) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}