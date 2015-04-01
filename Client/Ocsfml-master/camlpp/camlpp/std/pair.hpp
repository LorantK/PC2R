#ifndef CAMLPP_STD_PAIR_HPP_INCLUDED
#define CAMLPP_STD_PAIR_HPP_INCLUDED

extern "C"
{
#include <caml/mlvalues.h>
#include <caml/alloc.h>
}


#include <utility>


#include <camlpp/affectation_management.hpp>
#include <camlpp/conversion_management.hpp>
#include <camlpp/field_affectation_management.hpp>


namespace camlpp
{
  template<class T1, class T2>
  struct affectation_management< std::pair< T1, T2 > >
  {
    static void affect(value& v, std::pair< T1, T2 > const& p)
    {
      v = caml_alloc_tuple( 2 );
      field_affectation_management< T1 >::affect_field(v, 0, p.first);
      field_affectation_management< T2 >::affect_field(v, 1, p.second);
    }
  };

  template< class T1, class T2>
  struct conversion_management< std::pair< T1, T2 > >
  {
  private:
    conversion_management< T1 > cm1;
    conversion_management< T2 > cm2;
  public:
    std::pair< T1, T2 > from_value( value const& v )
    {
      assert( Is_block( v ) );
      assert( Tag_val( v ) == 0 );
      assert( Wosize_val( v ) == 2 );
      return std::make_pair( 	cm1.from_value( Field(v, 0 ) ),
				cm2.from_value( Field(v, 1 ) ) );
    }
  };
  

}

#endif
