function p = powerIteration(X, C=1000, epsilon=0.00001)
  % powerIteration, as defined:
  %   http://en.wikipedia.org/wiki/Principal_component_analysis#Computing_principal_components_iteratively
  % returns the first principle component
  % of X after at most C iterations of the
  % Power Iteration method, or earlier if
  % convergence within epsilon is detected.

  n = size(X, 1); % number of rows
  d = size(X, 2); % dimension of X
  p = rand(1, d);
  X = X';
  pLast = zeros(1, d);
  for c = 1:1000
    t = zeros(1, d);
    for i = 1:n
      x = X(i, :);
      t = t + dot(x, p) * x;
    endfor
    pLast = p;
    p = t / norm(t);
    if abs(max(p - pLast)) < epsilon
      break
    endif
  endfor
  p = p';
end
