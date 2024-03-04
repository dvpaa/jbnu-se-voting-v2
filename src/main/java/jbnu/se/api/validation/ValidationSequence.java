package jbnu.se.api.validation;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;
import jbnu.se.api.validation.ValidationGroups.FirstGroup;
import jbnu.se.api.validation.ValidationGroups.FourthGroup;
import jbnu.se.api.validation.ValidationGroups.SecondGroup;
import jbnu.se.api.validation.ValidationGroups.ThirdGroup;

@GroupSequence({Default.class, FirstGroup.class, SecondGroup.class, ThirdGroup.class, FourthGroup.class})
public interface ValidationSequence {
}
