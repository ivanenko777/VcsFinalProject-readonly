package lt.ivl.webInternalApp.utils;

import lt.ivl.components.service.RepairService;

public class UtilsHelper {
    private final RepairService componentRepairService = new RepairService();

    public static UtilsHelper getInstance() {
        return new UtilsHelper();
    }

    public boolean checkButtonVisibility(String currentStatus, String newStatus) {
        return componentRepairService.checkButtonVisibility(currentStatus, newStatus);
    }
}
