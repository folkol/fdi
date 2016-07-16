package com.folkol.di;

import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InjectorTest
{
    @Test
    public void createsInstance()
        throws Exception
    {
        Injector injector = new Injector();

        Service service = injector.create(Service.class);

        assertNotNull(service);
    }

    @Test
    public void injectDeepDependencies()
        throws Exception
    {
        Injector target = new Injector();

        Service service = target.create(Service.class);

        assertNotNull(service);
        assertNotNull(service.dependency);
        assertNotNull(service.dependency.dependency);
    }

    @Test
    public void injectConfiguredClass()
        throws Exception
    {
        Injector.Config cfg = new Injector.Config();
        cfg.bind(YetAnotherService.class).to(YetAnotherServiceImpl.class);
        Injector injector = new Injector(cfg);

        OtherService otherService = injector.create(OtherService.class);

        assertNotNull(otherService.dependency);
        assertEquals(YetAnotherServiceImpl.class, otherService.dependency.getClass());
    }

    @Test
    public void injectConfiguredInstance()
        throws Exception
    {
        Injector.Config cfg = new Injector.Config();
        YetAnotherServiceImpl yetAnotherService = new YetAnotherServiceImpl();
        cfg.bind(YetAnotherService.class).to(yetAnotherService);
        Injector injector = new Injector(cfg);

        OtherService otherService = injector.create(OtherService.class);

        assertTrue(yetAnotherService == otherService.dependency);
    }

    public static class Service
    {
        @Inject OtherService dependency;
    }

    public static class OtherService
    {
        @Inject YetAnotherService dependency;
    }

    public static class YetAnotherService
    {
    }

    public static class YetAnotherServiceImpl extends YetAnotherService
    {
    }
}
