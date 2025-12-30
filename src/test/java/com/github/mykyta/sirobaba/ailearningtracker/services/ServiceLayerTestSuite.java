package com.github.mykyta.sirobaba.ailearningtracker.services;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Created by Mykyta Sirobaba on 31.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Suite
@IncludeTags("Service")
@SuiteDisplayName("All Service Tests")
@SelectPackages("com.github.mykyta.sirobaba.ailearningtracker")
public class ServiceLayerTestSuite {
}
