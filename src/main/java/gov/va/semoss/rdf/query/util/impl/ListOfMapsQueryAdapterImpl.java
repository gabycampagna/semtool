/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.rdf.query.util.impl;

import java.util.HashMap;
import java.util.Map;
import org.openrdf.model.Value;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;

/**
 *
 * @author John Marquiss
 * @param <T>
 */
public abstract class ListOfMapsQueryAdapterImpl<T> extends ListQueryAdapter<Map<String, T>> {

  public ListOfMapsQueryAdapterImpl() {
  }

  public ListOfMapsQueryAdapterImpl( String sparq ) {
    super( sparq );
  }

  @Override
  public void handleTuple( BindingSet set, ValueFactory fac ) {
    Map<String, T> map = new HashMap<>();
    for ( String variable : set.getBindingNames() ) {
      map.put( variable, convertValue( variable, set.getValue( variable ), fac ) );
    }
    add( map );
  }

  protected abstract T convertValue( String variable, Value v, ValueFactory fac );

  public static ListOfMapsQueryAdapterImpl<String> forStrings( String sparql ) {
    ListOfMapsQueryAdapterImpl<String> q = new ListOfMapsQueryAdapterImpl<String>() {

      @Override
      protected String convertValue( String variable, Value v, ValueFactory fac ) {
        return v.stringValue();
      }
    };

    return q;
  }
}
