package replete.xstream;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SimpleStringListXStreamConverter implements Converter {

    public boolean canConvert(Class type) {
        return type.equals(ArrayList.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        List<String> list = (ArrayList<String>) source;
        for(int i = 0; i < list.size(); i++) {
            writer.startNode("element");
            writer.setValue(list.get(i));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        List<String> list = new ArrayList<>();
        while(reader.hasMoreChildren()) {
            reader.moveDown();
            list.add(reader.getValue());
            reader.moveUp();
        }
        return list;
    }
}