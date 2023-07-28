package replete.scripting.rscript.evaluation.functions;

// TODO: Technically this class shouldn't need to exist, as it
// is a mere duplicate of the ExponentiationFunction class, with
// a different name.  This alternate name really should be just
// a rendering change during the rendering of an AST.  However,
// at this point, the tree does not support custom rendering
// of just the value of a node.  It only supports the custom
// replacement of the value, and custom rendering of the whole
// node.  Thus this function is a way to bridge the gap.  We
// can replace the value of the ^ OpNodes with one of these
// to achieve terse rendering.
public class ExponentiationAltFunction extends ExponentiationFunction {
    @Override
    public String getName() {
        return "**";
    }
}
