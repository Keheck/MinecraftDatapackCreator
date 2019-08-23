package io.github.keheck.project.compiling;

class NumberGroup
{
    private int[] group;

    NumberGroup(int... nums)
    {
        group = nums;
    }

    int[] getGroup() { return group; }

    int get(int i) { return group[i]; }
}
