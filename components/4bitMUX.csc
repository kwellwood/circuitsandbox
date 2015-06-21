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
]><customComponent name="MUX (4)">
    <input x="40" y="0" />
    <input x="30" y="0" />
    <input x="0" y="20" />
    <input x="0" y="50" />
    <input x="0" y="40" />
    <input x="0" y="30" />
    <component type="not" id="4">
        <connection number="0" sourceId="1" sourcePin="0" />
    </component>
    <component type="or4" id="19">
        <connection number="0" sourceId="13" sourcePin="0" />
        <connection number="1" sourceId="12" sourcePin="0" />
        <connection number="2" sourceId="11" sourcePin="0" />
        <connection number="3" sourceId="16" sourcePin="0" />
    </component>
    <component type="and3" id="11">
        <connection number="0" sourceId="2" sourcePin="0" />
        <connection number="1" sourceId="4" sourcePin="0" />
        <connection number="2" sourceId="-1" sourcePin="4" />
    </component>
    <component type="and3" id="16">
        <connection number="0" sourceId="3" sourcePin="0" />
        <connection number="1" sourceId="4" sourcePin="0" />
        <connection number="2" sourceId="-1" sourcePin="3" />
    </component>
    <component type="not" id="3">
        <connection number="0" sourceId="2" sourcePin="0" />
    </component>
    <component type="and3" id="12">
        <connection number="0" sourceId="3" sourcePin="0" />
        <connection number="1" sourceId="1" sourcePin="0" />
        <connection number="2" sourceId="-1" sourcePin="5" />
    </component>
    <component type="not" id="2">
        <connection number="0" sourceId="-1" sourcePin="1" />
    </component>
    <component type="and3" id="13">
        <connection number="0" sourceId="2" sourcePin="0" />
        <connection number="1" sourceId="1" sourcePin="0" />
        <connection number="2" sourceId="-1" sourcePin="2" />
    </component>
    <component type="not" id="1">
        <connection number="0" sourceId="-1" sourcePin="0" />
    </component>
    <output x="60" y="30" sourceId="19" sourcePin="0" />
</customComponent>
