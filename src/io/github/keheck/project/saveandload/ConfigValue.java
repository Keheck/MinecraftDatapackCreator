package io.github.keheck.project.saveandload;

import static io.github.keheck.project.saveandload.ConfigValue.ValueType.*;

public class ConfigValue
{
    private String value;
    private ValueType type;

    public ConfigValue(String value)
    {
        this.value = value;

        if(value.toLowerCase().matches("(true|false)"))
            type = BOOL;
        else if(value.matches("-?\\d*\\.\\d+"))
        {
            try
            {
                Float.parseFloat(value);
                type = FLOAT;
            }
            catch(NumberFormatException ignore) {}
        }
        else if(value.matches("-?\\d+"))
        {
            try
            {
                Integer.parseInt(value);
                type = INTEGER;
            }
            catch(NumberFormatException ignore) { }
        }
        else
            type = STRING;
    }

    public String getValue() { return value; }

    public ValueType getType() { return type; }

    public enum ValueType
    {
        STRING, BOOL, INTEGER, FLOAT
    }
}
