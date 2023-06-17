package net.devtm.tmmobcoins.service;

public enum ServiceHandler {
    SERVICE;

    private final DataService dataService;

    private final LoggerService loggerService;

    private final EventService eventService;

    private final MenuService menuService;

    ServiceHandler() {
        this.menuService = new MenuService();
        this.eventService = new EventService();
        this.loggerService = new LoggerService();
        this.dataService = new DataService();
    }

    public MenuService getMenuService() {
        return this.menuService;
    }

    public EventService getEventService() {
        return this.eventService;
    }

    public LoggerService getLoggerService() {
        return this.loggerService;
    }

    public DataService getDataService() {
        return this.dataService;
    }
}
