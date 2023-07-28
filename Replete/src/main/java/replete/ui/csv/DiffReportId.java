package replete.ui.csv;

public enum DiffReportId {
        OLDER("Older", 1),
        NEWER("Newer", 2);

        private String label;
        private int id;
        private DiffReportId(String label, int id) {
            this.label = label;
            this.id = id;
        }
        public String getLabel() {
            return label;
        }
        public int getId() {
            return id;
        }
}
