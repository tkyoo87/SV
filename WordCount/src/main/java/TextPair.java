import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;


public class TextPair implements WritableComparable<TextPair> {

	private Text first;
	private Text second;
	
	public TextPair(){
		set(new Text(), new Text());
	}
	
	public TextPair(String first, String second){
		set(new Text(first), new Text(second));
	}
	
	public TextPair(Text first, Text second){
		this.first = first;
		this.second = second;
	}
	
	public void set(Text first, Text second){
		this.first = first;
		this.second = second;
	}
	
	public Text getFirst() {
		return first;
	}

	public Text getSecond() {
		return second;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		first.readFields(in);
		second.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		first.write(out);
		second.write(out);
		
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public int compareTo(TextPair tp) {
		int cmp = first.compareTo(tp.first);
		if(cmp !=0){
			return cmp;
		}
		return second.compareTo(tp.second);
	}
	

}
