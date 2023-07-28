package replete.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import replete.errors.RuntimeConvertedException;

public class TextTransformer {
    private List<UnaryOperator<String>> preCondTransformers = new ArrayList<>();
    private List<UnaryOperator<String>> postCondTransformers = new ArrayList<>();
    private List<Predicate<String>> conditions = new ArrayList<>();
    private List<Destination> destinations = new ArrayList<>();

    public TextTransformer addPreCondTransformer(UnaryOperator<String> preCondTransformer) {
        preCondTransformers.add(preCondTransformer);
        return this;
    }
    public TextTransformer addPostCondTransformer(UnaryOperator<String> postCondTransformer) {
        postCondTransformers.add(postCondTransformer);
        return this;
    }
    public TextTransformer addCondition(Predicate<String> condition) {
        conditions.add(condition);
        return this;
    }
    public TextTransformer addDestination(Destination destination) {
        destinations.add(destination);
        return this;
    }

    public void transform(File file) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            transform(reader);
        } catch(RuntimeConvertedException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    public void transform(BufferedReader reader) {
        try {
            for(Destination destination : destinations) {
                destination.begin();
            }
            String line;
            while((line = reader.readLine()) != null) {
                for(UnaryOperator<String> preCondTransformer : preCondTransformers) {
                    line = preCondTransformer.apply(line);
                }
                boolean skip = false;
                for(Predicate<String> condition : conditions) {
                    if(!condition.test(line)) {
                        skip = true;
                        break;
                    }
                }
                if(skip) {
                    continue;
                }
                for(UnaryOperator<String> postCondTransformer : postCondTransformers) {
                    line = postCondTransformer.apply(line);
                }
                for(Destination destination : destinations) {
                    destination.accept(line);
                }
            }
            for(Destination destination : destinations) {
                destination.end();
            }
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        File dir = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\Avondale\\src\\main\\java\\gov\\sandia\\avondale\\cluster\\ui\\jobs\\quick");
        File in = new File(dir, "quick3-dog-TRUNK-wcj3.params");
        File out = new File(dir, "quick3-dog-TRUNK-wcj3-out.params");
        TextTransformer transformer = new TextTransformer()
            .addDestination(new ConsoleDestination())
            .addDestination(new FileDestination(out))
        ;
        transformer.transform(in);
    }
}
