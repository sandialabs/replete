package replete.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import replete.errors.RuntimeConvertedException;
import replete.numbers.NumUtil;

// http://x-stream.github.io/converter-tutorial.html

public class ExtArrayListXStreamConverter implements Converter {
    public boolean canConvert(Class clazz) {
        return clazz.equals(ExtArrayList.class);
    }
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ExtArrayList<?> list = (ExtArrayList) value;
        writer.addAttribute("unique", "" + list.isRequireUniqueClasses());
        for(Object elem : list) {
            writer.startNode(elem.getClass().getName());
            context.convertAnother(elem);
            writer.endNode();
        }
    }
    public ExtArrayList<?> unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String uniqueAttr = reader.getAttribute("unique");
        boolean unique = NumUtil.isBoolean(uniqueAttr) ? NumUtil.b(uniqueAttr) : false;
        ExtArrayList list = new ExtArrayList<>(unique);
        while(reader.hasMoreChildren()) {
            reader.moveDown();
            Class clazz;
            try {
                clazz = Class.forName(reader.getNodeName());     // TODO: One day take into consideration
            } catch(ClassNotFoundException e) {                  // aliasing mechanisms.
                throw new RuntimeConvertedException(e);
            }
            Object elem = context.convertAnother(list, clazz);
            list.add(elem);
            reader.moveUp();
        }
        return list;
    }
}
