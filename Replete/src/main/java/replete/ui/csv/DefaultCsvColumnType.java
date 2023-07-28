package replete.ui.csv;

import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageModelConcept;

public enum DefaultCsvColumnType implements CsvColumnType {


    ///////////
    // ENUMS //
    ///////////

    DEFAULT("Default", RepleteImageModel.CSV_COL_OTHER, RepleteImageModel.CSV_COL_OTHER);


    ////////////
    // FIELDS //
    ////////////

    private String description;
    private ImageModelConcept parentIcon;
    private ImageModelConcept childIcon;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    private DefaultCsvColumnType(String description, ImageModelConcept parentIcon, ImageModelConcept childIcon) {
        this.description = description;
        this.parentIcon = parentIcon;
        this.childIcon = childIcon;
    }


    /////////////
    // GETTERS //
    /////////////

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ImageModelConcept getParentIcon() {
        return parentIcon;
    }

    @Override
    public ImageModelConcept getChildIcon() {
        return childIcon;
    }
}