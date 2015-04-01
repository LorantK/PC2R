#ifndef CAMLPP_STD_VECTOR_HPP_INCLUDED
#define CAMLPP_STD_VECTOR_HPP_INCLUDED

extern "C"
{
#include <caml/mlvalues.h>
#include <caml/alloc.h>
}

#include <vector>

#include <camlpp/affectation_management.hpp>
#include <camlpp/conversion_management.hpp>
#include <camlpp/field_affectation_management.hpp>
#include <camlpp/details/float_array_support.hpp>

namespace camlpp
{
  template<class T>
  struct affectation_management< std::vector< T > >
  {
    static void affect(value& v, std::vector<T> const& vec)
    {
      typedef details::float_array_support< std::tuple<T> > array_traits;
      
      v = caml_alloc( vec.size() * array_traits::word_size , array_traits::tag );
      for(int i = 0; i < vec.size(); ++i)
	{
	  field_affectation_management< T >::affect_field(v, i, vec[i]);
	}
    }
  };
  
  template<class T>
  struct conversion_management< std::vector< T > >
  {
  private:
    conversion_management< T > cm;
  public:
    std::vector< T > from_value( value const& v)
    {
      std::vector< T > res;
      res.reserve( Wosize_val( v ) );
      for(int i = 0; i < res.capacity(); ++i)
	{
	  res.push_back( cm.from_value( Field(v, i) ) );
	}
      return res;
    }
  };
}



#endif
