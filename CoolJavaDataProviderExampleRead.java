package coolsuites;

import helpers.PushMasterLogic;
import helpers.SVNCommands;
import helpers.XSRandom;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import scripts.PythonScripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nedim on 9/25/15.
 */
public class TestDataProviderTrick {

    /*
    I have created with some great teams at Intel creating Directed Automated Random Testing frameworks for 
    pre-Silicon testing of Chipsets.

    The idea is never test the same way and bombard the logic with in a controlled way.
    This way we can even use a single test file to run all tests. 

    I doubt anyone has written/used the TestNG DataProvider this way yet.
    
---- Why did I create this framework, please read on!

    In one of my projects for Backend QA Automation I was handed a Automation Framework.
    This Framework had 80 different test cases already created.
    The project kept changing its definition anytime I had to modify 80 tests that looked pretty similar.
    Other than the pain changing these files, duplicate code is a no no when coding.

    What I did was to take TestNG to the next level of testing by creating a single test file 
    That can handle all test cases and hammer the API.  

    This framework in it's general testing mode will never repeat the same test 
    unless we use the same seed while generating random choices.

    API to be tested:  This is a file mover.  server A to B.  
    Server A: Is a Git Repo, in our testing we have 500 files and multiple directories with sub directories.
              
    Server B: Is just a directory.  This directory may have connection with an SVN Repo.

    API makes a copy of server A to server B while making sure server B content is identical to server.
    API adds and deletes files on server B to make server B a copy of server A.
    There are three methods, copy, sftp, and svn.
    API deletes any extra files on server B that are not in a protected directoy defined in json file.--DELETE Scenario
    API adds any extra that it has to server B.-- ADD Scenario
    API adds any file that was changed on server A to server B.--MODIFY Scenario

    This framework is capable of assigning any files for the API to act upon by 
    -- Deleting files on server A
    -- Adding files on server A
    -- Modifying files on server A

    Number of files and which files are acted upon can be directly controlled down to the specific file.
    Or the test can make these decisions based on a random number generator.
    Warning:  Random number generator in Java isn't as random.  And reproducing the same results is key to debug.
              How to reproduce a test? Use a seed while generating a random number.              
              Every time you ask for a random number let's say 1-5 with java, it will always create the same numbers.
              That is why we use a current timestamp to use a seed to generate random numbers.
              It is also wise to send the seed through an x-or shifter of a sort.

    Data Provider fields are called knobs.  Have seen an old receiver w/ a bunch of knobs?
    Twist treble knob to the left less treble, same w/ the bass knob.

    They work the same. Enough of reading let's look at this data provider.
    Let's remember Data Provider can take lists, hashtables anything to pass as an object.
    All names below have been changed... Thank you for reading 
    Nedim Tosyali.
*/

    @DataProvider(name = "input")
    public Object[][] data() {
        /*Git branch(single entry) Golden data source for server A
          Locale(single entry)
          Region/method(single entry)
                         "copy"         = copy method
                         "sftp"         = sftp method
                         ""             = SVN1
                         "<someRegion>" = SVN2
          Branch or Trunk(single entry)
          Sitename(single entry)
          FileOperationsTable(this is a two dimensional list)
          Cut Ratio(3 member list)-- list(0-2),
                                     if list0 = 25 => 25% of files and folders in Site will be used for delete scenario
                                     if list1 = 25 => 25% of files and folders in Site after delete
                                     will be used for modify scenario
                                     if list2 = 25 => 25% of files and folders in Site after modify
                                     will be used for add scenario
          Seed(single entry)-- to use for reproducing specific scenarios.
        {"downloads776K-enCA", "en_CA", "sftp", "trunk", "downloads776K", tableNullnoDel, randomCuts, ""}  //****

        */

        List<String> listWorkFiles = new ArrayList<String>();
        listWorkFiles.add("/dir1/docs/junk1.zip");//7.2M
        listWorkFiles.add("/dir2/docs/movieFile.m4v");//16M
        listWorkFiles.add("/dir3/images"); //subdirectory

        List<String> generalSimpleStatic = new ArrayList<String>();
        generalSimpleStatic.add("/static/test2.txt");

        //each row is a list to use for different operations
        // in this concept the first member of the row tells the operation

        String[][] table_docs211M_zhCN = {
                {"rm"} //random files will be removed from Server A copy
                ,{"modify","apps/images/all_activity.jpg", "what-is/index.html"}//  these two files will be modified(changed) on server A
                ,{"add","apps/images/","recovery/", "images"}//  add extra directories to server A
        };

        String[][] table_docs211M_enCA_22981368 = {
                {"rm"}
                ,{"modify"}
                ,{"add","styles/", "apps/images/", "all_activity.jpg"}
        };
        String[][] table_downloads776K_enCA__22981368 = {
                {"rm", "images/developer-headline_image.png", "moremoremore"}//remove one specific file from Server A and remove random files and dirs.
                ,{"modify","images/subhead_image.png"}// modify only one file on Server A
                ,{"add"}
        };

        String[][] table_docs184M_enCA = {
                {"rm"}
                ,{"modify"}
                ,{"add","itunes.apple.com/za/", "", "APD-Med-Res-Catalog-May2010.pdf"}
        };

        String[][] tableNull = { // All random!
                {"rm"}
                ,{"add"}
                ,{"modify"}
        };


        String[][] tableNullnoDel = {//only random adds and modifies
                {"add"}
                ,{"modify"}
        };
        String[][] tabledelItunes = { //  only remove one file. random adds, modifies
                {"rm", "junk.com"},
                {"add"}
                ,{"modify"}
        };

        //There are 3 types of transactions
        //Delete, add, modify
        //The properties for protected and other files differ.
        //randomCuts: if there are 56 files, and cut is 25: 56*25/100 = 14 files will be used
        List<Integer> randomCuts = new ArrayList<Integer>();
        randomCuts.add(25);
        randomCuts.add(25);
        randomCuts.add(1);

        List<Integer> randomCuts184MenCA = new ArrayList<Integer>();
        randomCuts184MenCA.add(50);
        randomCuts184MenCA.add(40);
        randomCuts184MenCA.add(25);

        List<Integer> randomCutsitunes99MenHK = new ArrayList<Integer>();
        randomCutsitunes99MenHK.add(50);
        randomCutsitunes99MenHK.add(40);
        randomCutsitunes99MenHK.add(25);

        return new Object[][]{

                 {"docs29M-enUS", "en_US", "copy", "trunk", "docs29M", tableNullnoDel, randomCuts, ""}
                ,{"docs184M-enCA", "en_CA", "copy", "ic01", "docs184M", tableNull, randomCutsitunes99MenHK, ""}
                ,{"docs23M-frCA", "fr_CA", "copy", "ic01", "docs23M", tableNull, randomCuts184MenCA, ""}
                ,{"itunes99M-enHK", "en_HK", "copy", "trunk", "itunes99M", tableNull, randomCutsitunes99MenHK, ""}
                ,{"docs29M-enUS", "en_US", "sftp", "trunk", "docs29M", tableNull, randomCuts, ""}
                ,{"docs211M-zhCN", "zh_CN", "sftp", "trunk", "docs211M", table_docs211M_zhCN, randomCuts, ""}
                ,{"downloads776K-enCA", "en_CA", "sftp", "trunk", "downloads776K", tableNullnoDel, randomCuts, ""}
                ,{"itunes99M-enHK", "en_HK", "sftp", "trunk", "itunes99M", tableNull, randomCutsitunes99MenHK, ""}
                ,{"docs23M-frCA", "fr_CA", "sftp", "ic01", "docs23M", tableNull, randomCuts184MenCA, ""}
                ,{"docs184M-enCA", "en_CA", "", "ic01", "docs184M", tableNull, randomCuts184MenCA, ""}
                ,{"downloads776K-enCA", "en_CA", "", "trunk", "downloads776K", tableNull, randomCuts, ""}
                ,{"docs40M-enUS", "en_US", "", "trunk", "docs40M", tableNull, randomCuts184MenCA, ""}
                ,{"docs40M-enUS", "en_US", "", "ic01", "docs40M", tableNull, randomCuts184MenCA, ""}
                ,{"docs23M-frCA", "fr_CA", "", "ic01", "docs23M", tableNull, randomCuts184MenCA, ""}
                ,{"itunes99M-enHK", "en_HK", "", "trunk", "itunes99M", tableNull, randomCuts, ""}
                ,{"osx30M-zhHK", "zh_HK", "", "ic01", "osx30M", tableNull, randomCutsitunes99MenHK, ""}
                ,{"docs184M-enCA", "en_CA", "canada", "ic01", "docs184M", tableNull, randomCuts184MenCA, ""}
                ,{"downloads776K-enCA", "en_CA", "canada", "trunk", "downloads776K", tableNull, randomCuts, ""}
                ,{"docs40M-enUS", "en_US", "us", "trunk", "docs40M", tableNull, randomCuts184MenCA, ""}
                ,{"docs40M-enUS", "en_US", "us", "ic01", "docs40M", tableNull, randomCuts184MenCA, ""}
                ,{"docs23M-frCA", "fr_CA", "canada", "ic01", "docs23M", tableNull, randomCuts184MenCA, ""}
                ,{"itunes99M-enHK", "en_HK", "gc", "trunk", "itunes99M", tableNull, randomCuts, ""}
                ,{"osx30M-zhHK", "zh_HK", "gc", "ic01", "osx30M", tableNull, randomCutsitunes99MenHK, ""} 
        };
    }


    @Test(dataProvider="input")
    public void testPMFRandom (String gitBranch, String buildLocale, String region, String branch, String site, String[][] fileList, List<Integer> randomCuts, String seedAsString) throws IOException, JSONException, ParseException {

        //-------------------------Test Set Up-----------------------------------
        List<Integer> randomWeights = new ArrayList<Integer>();
        randomWeights.add(0);
        randomWeights.add(0);
        randomWeights.add(0);

        PushMasterLogic pml = new PushMasterLogic();
        if (seedAsString.isEmpty()) {
            seed = pml.getLongSeed();
        } else {
            seed = Integer.parseInt(seedAsString);
        }

        XSRandom rand = new XSRandom(seed);
        pml.setRandom(seed);
        pml.setGitBranch(gitBranch);
        pml.setBuildLocale(buildLocale);
        pml.setClassicRegion(region);
        pml.setPmfMethod(region);
        pml.setSite(site);
        pml.setBranch(branch);
        pml.setCopySlaveOn(true);
        pml.setSftpSlaveOn(true);
        pml.setSvnSlaveOn(true);
        pml.setSlaveOverride(slaveOverride);
        pml.setSlaveOnCutOff(slaveOnCutOff);
        pml.setProtDirExceptionFromBuildOnly(protDirExceptionFromBuildOnly);

        pml.setUpJenkinsJob();
        server = pml.getServer();

        if(!randomCuts.isEmpty()){
            randomWeights = randomCuts;
        }

        pml.downloadExpectedDirStrAndSetUp(regressionDirBase);
        setFileNames(site);
        regressionDir = pml.getRegressionDir();
        pml.populateWorkArea();
        pml.setProtDirKnobs(deleteProtectedDirs, modifyProtectedDirs);
        pml.createProtDirList();

        workSiteDir = pml.getWorkSiteDir();
        protectedSiteDir = workSiteDir+ "/../";
        expectedSiteDir = (pml.getExpectedDir() + "/" + pml.getRemotePath()).replace("//", "/");

        modFileListRm = pml.getListFileFromArray(fileList, "rm");
        modFileListModify = pml.getListFileFromArray(fileList, "modify");
        modFileListAdd = pml.getListFileFromArray(fileList, "add");
        //-----------------------------------------End of Test Setup-------------------------
        //-----------------------------------------Change Data-------------------------------
        if(pml.getDoRM()) {
            pml.modifyFiles(workSiteDir, fileListGold, false, pml, "rm", modFileListRm, randomWeights.get(0));
            if (pml.getIsSVN()) {
                SVNCommands.svnCommands(workSiteDir, false, "", true, workSiteDir);
            }
        }
        if(pml.getDoModify()) {
            pml.modifyFiles(workSiteDir, modFileListGold, false, pml, "modify", modFileListModify, randomWeights.get(1));
        }

        if (pml.getDoAdd()) {
            pml.modifyFiles(workSiteDir, modFileListGold, false, pml, "add", modFileListAdd, randomWeights.get(2));
        }
        //-----------------------------------------End of Change Data-------------------------
        modFileListRm.clear();
        modFileListModify.clear();
        modFileListAdd.clear();

        pml.copyProtDirs(expectedSiteDir, workSiteDir);
        pml.loadData();  // verify changes are in remote folders

        pml.runJenkinsJob(pml.getRepoURL());
        pml.getData();  //get remote folder content to compare with our expected data

        System.out.println("Comparing ExpectedDir = " + pml.getExpectedDir() + "\n with After changes Export = " + pml.getCopyAfterPMF());
        PythonScripts.compareFolders(pml.getExpectedDir(), pml.getCopyAfterPMF());
        System.out.println("Test ended");
        regressionDirCleanList.add(regressionDir);
    }

    public void setFileNames(String site) {
        fileListGold = regressionDir+ site+"Files.txt";
        fileListWork = regressionDir+ site+"FilesWork.txt";
        modFileListGold = regressionDir + site+ "ModFiles.txt";
        modFileListWork = regressionDir+ site+"ModFilesWork.txt";
    }

    @AfterMethod
    public void cleanUp() {
        if (cleanRegressionDir) {
            System.out.println("Deleting regression directory at " + regressionDir);
            for(String toClean:regressionDirCleanList){
                PushMasterLogic.cleanUpRegressionDir(toClean);
                PushMasterLogic.cleanUpRegressionDir("*Work*txt");
                PushMasterLogic.cleanUpRegressionDir(regressionDir);
            }
        } else {
            System.out.println("You have chosen not to to delete the regression directory \n " +
                    "Your regression directory is "+ regressionDir);
        }
        regressionDirCleanList.clear();
        }
    }
