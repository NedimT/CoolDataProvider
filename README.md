/**
 * Created by Nedim on 9/25/15.
 */

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

