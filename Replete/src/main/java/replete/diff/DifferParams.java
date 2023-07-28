package replete.diff;

import replete.collections.TrackedBean;

// This class inherits from TrackedBean so that all pipeline
// component parameters can have a consistent hierarchy via
// PipelineComponentParams.  However, since extractors and
// analyzers are StatelessProcesses and not PersistentControllers,
// they don't actually need tracked bean functionality.
// [Class Type: Structural/Mid, Empty]
public abstract class DifferParams extends TrackedBean {

    // No methods currently needed (hashCode/equals handled by base classes)

}
