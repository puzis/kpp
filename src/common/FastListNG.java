package common;

import javolution.util.FastList;

public class FastListNG<E> extends FastList<E> {
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean equals(Object obj){
		if (this==obj)
			return true;
		if (obj==null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FastListNG<E> other = (FastListNG<E>) obj;
		if (this.size()!=other.size())
			return false;
		for(FastList.Node<E> node=this.head(), end=this.tail(); (node = node.getNext())!=end;){
			E nodeVal = node.getValue();
			if (!other.contains(nodeVal))
				return false;
		}
		return true;
	}
}
