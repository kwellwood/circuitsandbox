<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE customComponent [
   <!ELEMENT customComponent (input+, component*, output+)>
   <!ATTLIST customComponent
       name CDATA #REQUIRED>
   <!ELEMENT input EMPTY>
   <!ATTLIST input
       x CDATA #REQUIRED
       y CDATA #REQUIRED>
   <!ELEMENT component (connection*)>
   <!ATTLIST component
       type CDATA #REQUIRED
       id CDATA #REQUIRED>
   <!ELEMENT connection EMPTY>
   <!ATTLIST connection
       number CDATA #REQUIRED
       sourceId CDATA #REQUIRED
       sourcePin CDATA #REQUIRED>
   <!ELEMENT output EMPTY>
   <!ATTLIST output
       x CDATA #REQUIRED
       y CDATA #REQUIRED
       sourceId CDATA #REQUIRED
       sourcePin CDATA #REQUIRED>
]><customComponent name="Wire Splitter">
    <input x="0" y="10" />
    <output x="20" y="10" sourceId="-1" sourcePin="0" />
</customComponent>
