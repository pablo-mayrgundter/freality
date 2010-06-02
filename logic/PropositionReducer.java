package logic;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple proposition reducer.
 *
 * @author Pablo Mayrgundter
 */
public class PropositionReducer {

  /**
   * A proposition is either true or false if it is a simple
   * proposition, or else it is predicated on a variable.  If the
   * given proposition is simple, either true or false will be
   * returned, otherwise a predicate, possibly compound, will be
   * returned.
   */
  public static Proposition reduce(Proposition prop) {
    if (prop instanceof HasVariable) {
      return reduce((HasVariable) prop);
    } else {
      if (prop.isTrue())
        return new True(); // Base case.
      return new False(); // Base case.
    }
  }

  /**
   * A HasVariable may reduce if at least one subproposition is an
   * operator.
   */
  public static Proposition reduce(HasVariable hv) {
    if (hv instanceof Operator) {
      return reduce((Operator) hv);
    } else if (hv instanceof Predicate) {
      return (Predicate) hv; // Base case.
    }
    throw new IllegalStateException("Unhandled reduction for type: " + hv.getClass());
  }

  /**
   * An Operator may reduce if at least one subproposition is not a
   * HasVariable.
   */
  public static Proposition reduce(Operator o) {
    if (o instanceof Conjunction) {
      return reduce((Conjunction) o);
    } else if (o instanceof Disjunction) {
      return reduce((Disjunction) o);
    }
    throw new IllegalStateException("Unhandled reduction for type: " + o.getClass());
  }

  /**
   * A conjunction is true iff all of its propositions are true.
   * This method will reduce the given conjunction by removing any
   * subtrees that are non-predicated truths, or will simply return
   * false if any subtree evaluates to false.
   */
  public static Proposition reduce(Conjunction c) {
    final Proposition [] props = c._propositions;
    final List<Proposition> toKeep = new ArrayList<Proposition>();
    for (int i = 0; i < props.length; i++) {
      final Proposition prop = reduce(props[i]);
      if (prop instanceof HasVariable) {
        toKeep.add(prop);
      } else {
        // Reduction!
        if (!prop.isTrue())
          return new False();
        // else drop
      }
    }
        
    // Totally reduced.
    if (toKeep.size() == 0)
      return new True();

    // Partially reduced.
    if (toKeep.size() == 1)
      return reduce(toKeep.get(0));

    Conjunction ret = new Conjunction(toKeep.toArray(new Proposition[toKeep.size()]));
    return ret;
  }

  /**
   * A disjunction is true iff any of its propositions are true.
   * This method will reduce the given disjunction by simply
   * returning true if any subtree evaluates to true, false if all
   * subtrees evaluate to false, or a predicate excluding any false
   * subtrees.
   */
  public static Proposition reduce(Disjunction d) {
    final Proposition [] props = d._propositions;
    boolean sawTrue = false;
    final List<Proposition> toKeep = new ArrayList<Proposition>();
    for (int i = 0; i < props.length; i++) {
      final Proposition prop = reduce(props[i]);
      if (prop instanceof HasVariable) {
        toKeep.add(prop);
      } else {
        // Reduction!
        if (prop.isTrue())
          sawTrue = true;
        // else drop
      }
    }

    // Totally reduced.
    if (toKeep.size() == 0) {
      if (sawTrue)
        return new True();
      else
        return new False();
    }

    // Partially reduced.
    if (toKeep.size() == 1)
      return reduce(toKeep.get(0));

    return new Disjunction(toKeep.toArray(new Proposition[toKeep.size()]));
  }

  /**
   * A Rule is true iff one of its possible implications is true, or
   * if the condition can be evaluated and its implication is true.
   * This method will reduce the given rule by reducing its
   * condition and implications as needed and possibly simplifying
   * the entire expression to a simple proposition.
   */
  public static Proposition reduce(Rule r) {
    Proposition condition = r.getCondition();
    condition = reduce(condition);
    if (condition instanceof HasVariable) {
      return new Rule(condition, reduce(r.getImplication()), reduce(r.getNegativeImplication()));
    } else {
      if (condition.isTrue()) {
        return reduce(r.getImplication());
      } else {
        return reduce(r.getNegativeImplication());
      }
    }
  }
}
