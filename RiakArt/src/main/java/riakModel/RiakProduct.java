package riakModel;

import java.util.Comparator;

import com.basho.riak.client.api.commands.kv.UpdateValue;

public class RiakProduct{
    	//Integer was used by the example, may be solution if it crashes later down the line.
	//no getters and setters. The example didn't show any and perhaps this format is needed to conversion to riak.
	//don't fix it if it isnt broke.
	public String key;
    public String omschrijving;
    public int inhoudaantal;
    
    
    public RiakProduct(){
    	
    }
	
	public RiakProduct(String key, String omschrijving, int inhoudaantal) {
		super();
		this.key = key;
		this.omschrijving = omschrijving;
		this.inhoudaantal = inhoudaantal;;
	}
	
	public static Comparator<RiakProduct> COMPARE_BY_KEY = new Comparator<RiakProduct>() {
        public int compare(RiakProduct one, RiakProduct other) {
            return one.key.compareTo(other.key);
        }
    };
    
    //Liever doe ik dit niet met een inner static class. Maar zo doet de tutorial het. Ik weet momenteel nog niet
    //hoe ik genoeg informatie uit riak kan krijgen om te bepalen of het zo nodig is. Misschien dat er ook een delete product
    //extension is. 2 extensions kunnen niet wat een aparte classe nodig maakt.
    public static class ProductUpdate extends UpdateValue.Update<RiakProduct> {
        private final RiakProduct update;
        public ProductUpdate(RiakProduct update){
            this.update = update;
        }

        @Override
        public RiakProduct apply(RiakProduct t) {
            if(t == null) {
                t = new RiakProduct();
            }

            t.key = update.key;
            t.omschrijving = update.omschrijving;
            t.inhoudaantal = update.inhoudaantal;
            return t;
        }
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RiakProduct other = (RiakProduct) obj;
		if (inhoudaantal != other.inhoudaantal)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (omschrijving == null) {
			if (other.omschrijving != null)
				return false;
		} else if (!omschrijving.equals(other.omschrijving))
			return false;
		return true;
	}
}
    