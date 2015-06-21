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
]><customComponent name="DEMUX (4)">
    <input x="0" y="30" />
    <input x="20" y="0" />
    <input x="30" y="0" />
    <component type="and3" id="15">
        <connection number="0" sourceId="4" sourcePin="0" />
        <connection number="1" sourceId="7" sourcePin="0" />
        <connection number="2" sourceId="-1" sourcePin="0" />
    </component>
    <component type="not" id="4">
        <connection number="0" sourceId="-1" sourcePin="1" />
    </component>
    <component type="and3" id="16">
        <connection number="0" sourceId="6" sourcePin="0" />
        <connection number="1" sourceId="7" sourcePin="0" />
        <connection number="2" sourceId="-1" sourcePin="0" />
    </component>
    <component type="not" id="7">
        <connection number="0" sourceId="5" sourcePin="0" />
    </component>
    <component type="and3" id="12">
        <connection number="0" sourceId="4" sourcePin="0" />
        <connection number="1" sourceId="5" sourcePin="0" />
        <connection number="2" sourceId="-1" sourcePin="0" />
    </component>
    <component type="not" id="6">
        <connection number="0" sourceId="4" sourcePin="0" />
    </component>
    <component type="and3" id="14">
        <connection number="0" sourceId="6" sourcePin="0" />
        <connection number="1" sourceId="5" sourcePin="0" />
        <connection number="2" sourceId="-1" sourcePin="0" />
    </component>
    <component type="not" id="5">
        <connection number="0" sourceId="-1" sourcePin="2" />
    </component>
    <output x="60" y="50" sourceId="16" sourcePin="0" />
    <output x="60" y="40" sourceId="15" sourcePin="0" />
    <output x="60" y="20" sourceId="12" sourcePin="0" />
    <output x="60" y="30" sourceId="14" sourcePin="0" />
</customComponent>
