package batfish.representation.juniper;

import batfish.representation.LineAction;
import batfish.representation.Prefix;
import batfish.representation.RouteFilterLengthRangeLine;
import batfish.representation.RouteFilterList;
import batfish.util.SubRange;

public final class RouteFilterLineExact extends RouteFilterLine {

   /**
    *
    */
   private static final long serialVersionUID = 1L;

   public RouteFilterLineExact(Prefix prefix) {
      super(prefix);
   }

   @Override
   public void applyTo(RouteFilterList rfl) {
      int prefixLength = _prefix.getPrefixLength();
      RouteFilterLengthRangeLine line = new RouteFilterLengthRangeLine(
            LineAction.ACCEPT, _prefix.getAddress(), prefixLength,
            new SubRange(prefixLength, prefixLength));
      rfl.addLine(line);
   }

}