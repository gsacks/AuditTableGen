/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.certifi.audittablegen;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author jputney
 */
public class TriggerBuilderTest {
    TriggerBuilder builder = new TriggerBuilder();
    TriggerConfiguration triggerConf = mock(TriggerConfiguration.class);
    DbSchemaRepository dbRepo = mock(DbSchemaRepository.class);
    
    @Before
    public void setup() {
        builder.triggerConfiguration = triggerConf;
        builder.dbSchemaRepository = dbRepo;
    }
    
    @Test
    public void testBuildTrigger() {
        when(triggerConf.skipTable("testTable")).thenReturn(Boolean.TRUE);
        
        builder.buildTrigger("testTable");
        
        verifyNoMoreInteractions(dbRepo);
        verify(dbRepo,times(0)).doMagicTriggerCreation("testTable");
        
        reset(triggerConf, dbRepo);
        builder.buildTrigger("test2");
        verify(dbRepo).doMagicTriggerCreation(any(String.class));
    }
}