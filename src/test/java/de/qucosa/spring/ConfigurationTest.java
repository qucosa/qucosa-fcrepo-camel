package de.qucosa.spring;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class ConfigurationTest {
    private GenericApplicationContext ctx = new GenericApplicationContext();
    
    @Test
    public void Home_dir_system_property() throws IOException {
        PropertiesFactoryBean bean = (PropertiesFactoryBean) ctx.getBean("appProperties");
        System.out.println(bean.getObject().getProperty("${home.dir}"));
        ctx.close();
    }
}
