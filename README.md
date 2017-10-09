Install SWI prolog from this link.
http://www.swi-prolog.org/download/stable
Add "jpl.jar" file, found in the bin folder inside the installation directory, as an external jar file to the project. 
Add the jar files as per the "Libs.png", from the downloaded stanford NLP, inside the lib library of the project and build them manually.
Create the following two environment variables with as per your computer

1- name: SWI_HOME_DIR, value: C:\Program Files\swipl
2- name: PATH , value: %SWI_HOME_DIR%\bin;%SWI_HOME_DIR%\lib\jpl.jar

Enjoy :)
