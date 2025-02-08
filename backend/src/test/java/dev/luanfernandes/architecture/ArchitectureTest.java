package dev.luanfernandes.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@AnalyzeClasses(packages = "dev.luanfernandes")
class ArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("dev.luanfernandes");

    @Test
    void controllersShouldResideInControllerPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Controller")
                .should()
                .resideInAPackage("..controller..")
                .check(importedClasses);
    }

    @Test
    void repositoriesShouldResideInRepositoryPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .resideInAPackage("..repository..")
                .check(importedClasses);
    }

    @Test
    void servicesShouldResideInServicePackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Service")
                .should()
                .resideInAPackage("..service..")
                .check(importedClasses);
    }

    @Test
    void webclientsShouldResideInWebClientPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Client")
                .should()
                .resideInAPackage("..webclient..")
                .check(importedClasses);
    }

    @Test
    void configsShouldResideInConfigPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Config")
                .should()
                .resideInAPackage("..config..")
                .check(importedClasses);
    }

    @Test
    void constantsShouldResideInConstantsPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Constants")
                .should()
                .resideInAPackage("..constants..")
                .check(importedClasses);
    }

    @Test
    void noClassesShouldUseFieldInjection() {
        ArchRuleDefinition.noFields()
                .should()
                .beAnnotatedWith(Inject.class)
                .orShould()
                .beAnnotatedWith(Autowired.class)
                .check(importedClasses);
    }

    @Test
    void mappersShouldResideInMapperPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Mapper")
                .should()
                .resideInAPackage("..domain.mapper..")
                .check(importedClasses);
    }

    @Test
    void requestsShouldResideInRequestPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Request")
                .should()
                .resideInAPackage("..domain.request..")
                .orShould()
                .resideInAPackage("..webclient.request..")
                .check(importedClasses);
    }

    @Test
    void responseShouldResideInResponsePackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Response")
                .should()
                .resideInAPackage("..domain.response..")
                .orShould()
                .resideInAPackage("..webclient.response..")
                .check(importedClasses);
    }

    @Test
    void servicesShouldNotCallControllers() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..service..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .check(importedClasses);
    }

    @Test
    void webclientsShouldOnlyBeCalledByServicesOrMappersOrTests() {
        ArchRuleDefinition.classes()
                .that()
                .resideInAPackage("..webclient..")
                .should()
                .onlyHaveDependentClassesThat()
                .resideInAnyPackage("..service..", "..domain.mapper..", "..webclient..")
                .check(importedClasses);
    }

    @Test
    void controllersShouldNotDependOnEntities() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..domain.entity..")
                .check(importedClasses);
    }
}
